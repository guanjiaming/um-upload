<template>
  <div class="um-upload">
    <div
        class="um-upload__content"
        @click="onOpenFileDialog"
        tabindex="0"
    >

      <template v-if="drag">
        <um-upload-dragger @file-drop="onDrop">
          <slot></slot>
        </um-upload-dragger>
      </template>

      <template v-else>
        <slot></slot>
      </template>

      <input
          type="file"
          class="um-upload__input"
          :multiple="multiple"
          name="file"
          ref="uploadInput"
          tabindex="-1"
          @change="onFileInputChange"
          @click.stop
      >
    </div>

    <um-upload-list :file-list="fileList"></um-upload-list>

  </div>
</template>
<script setup>
import {ref, shallowRef} from "vue";

import md5Worker from "../workers/getFileMd5?worker";

import UmUploadDragger from "./UmUploadDragger.vue";
import UmUploadList from "./UmUploadList.vue";
import TaskPool from "../utils/Concurrent";

const props = defineProps({
  action: {
    type: String,
    required: true
  },
  drag: {
    type: Boolean,
    default: false
  },
  method: {
    type: String,
    default: "post"
  },
  multiple: {
    type: Boolean,
    default: false
  },
  limit: Number,
  onSuccess: Function,
  onProgress: Function,
  onError: Function,
  secondUpload: String, // 是否秒传
});

/* input元素 */
let uploadInput = ref(null);

const fileList = ref([]);
const requests = shallowRef({});
// const workerQueue = new Queue();

/* input change 事件 */
const onFileInputChange = (e) => {
  let files = e.target.files;
  if (!files) return;
  files = Array.from(files);
  onUploadFiles(files);
}

/**
 * onDrop事件
 */
function onDrop(e) {

  if (!e.dataTransfer) return;

  const files = Array.from(e.dataTransfer.files);

  onUploadFiles(files);
}

/**
 * 上传文件逻辑
 * @param files
 */
function onUploadFiles(files) {
  if (files.length === 0) return;

  if (props.limit && fileList.value.length + files.length > props.limit) {
    alert("上传文件数量超出限制！");
    return;
  }

  if (!props.multiple) {
    files = files.slice(0, 1);
  }

  const concurrent = new TaskPool(2);

  for (const file of files) {
    const rawFile = file;
    rawFile.id = Date.now();

    const oFile = {
      name: rawFile.name,
      percentage: 0,
      status: 'ready',
      size: rawFile.size,
      raw: rawFile,
      id: rawFile.id,
      response: null
    }

    concurrent.addTask(getFileMd5ForWorker(rawFile)).then(res => {

      if (res) oFile.md5 = res;
      console.log("md5:res:", res)

      fileList.value.push(oFile);

      uploadInput.value.value = "";

      doUploadFiles(rawFile);
    })
  }
}

async function doUploadFiles(rawFile) {
  const file = getFile(rawFile);

  const option = {
    filename: "file",
    file: rawFile,
    method: props.method,
    action: props.action,
    onProgress: (e, percent) => {
      file.status = "uploading";
      file.percent = percent;
      props.onProgress && props.onProgress(percent);
    },
    onSuccess: (res) => {
      file.status = 'success'
      file.response = res;
      props.onSuccess && props.onSuccess(res);
      delete requests.value[rawFile.id]
    },
    onError: (err) => {
      props.onError && props.onError(err);
      delete requests.value[rawFile.id]
    },
  };

  // 文件秒传
  if (props.secondUpload && file.md5) {
    const res = await verifyRequest("get", `${props.secondUpload}/${file.md5}`);
    console.log("秒传：", res);
    if (res) {
      option.onProgress(null, 100);
      setTimeout(() => {
        option.onSuccess(res);
      }, 100)
      return;
    }
  }

  requests.value[rawFile.id] = uploadRequest(option);
}

function getFile(rawFile) {
  return fileList.value.find(item => item.id === rawFile.id);
}

/* 打开文件上传选择 */
const onOpenFileDialog = () => {
  uploadInput.value.click();
}

/**
 * 上传文件请求
 */
function uploadRequest(option) {

  const xhr = new XMLHttpRequest();

  xhr.open(option.method, option.action, true);

  /* 各事件监听 */
  xhr.upload.addEventListener("progress", function (e) {
    if (e.lengthComputable) {
      let percent = Math.round(e.loaded / e.total * 100);
      option.onProgress(e, percent);
      console.log("上传进度" + percent);
    }
  });

  xhr.addEventListener("load", function (e) {
    if (xhr.status < 200 || xhr.status >= 300) {
      option.onError(xhr.response.error || xhr.response || xhr.responseText || `上传失败：${option.action} ${xhr.status}`);
      return;
    }

    const text = e.target.responseText || e.target.response
    if (!text) {
      option.onSuccess(text);
      return;
    }
    try {
      option.onSuccess(JSON.parse(text));
    } catch {
      option.onSuccess(text);
    }
  });

  xhr.addEventListener("error", function () {
    option.onError(xhr.response.error || xhr.response || xhr.responseText || `上传失败：${option.action} ${xhr.status}`);
  });

  const formData = new FormData();
  formData.append(option.filename, option.file)

  xhr.send(formData);

  return xhr;
}

/**
 * 校验服务器文件是否己存在
 * @param method
 * @param url
 * @returns {Promise<unknown>}
 */
function verifyRequest(method, url) {
  return new Promise(function (resolve, reject) {
    const xhr = new XMLHttpRequest();

    xhr.open(method, url, true);

    xhr.onreadystatechange = function () {
      if (xhr.readyState === 4) {
        if (xhr.status < 200 || xhr.status >= 300) {
          reject(xhr.status);
        } else {
          const text = xhr.responseText || xhr.response
          if (!text) {
            resolve(text);
            return;
          }
          try {
            resolve(JSON.parse(text));

          } catch {
            resolve(text);
          }
        }
      }
    }

    xhr.send(null);
  })
}

/**
 * 通过线程获取
 */
function getFileMd5ForWorker(file) {
  return function () {
    return new Promise(function (resolve, reject) {
      const worker = new md5Worker();
      worker.postMessage(file)
      worker.addEventListener("message", function (e) {
        resolve(e.data);
      })
    })
  }
}

</script>
<style>

.um-upload__content {
  justify-content: center;
  align-items: center;
  cursor: pointer;
  outline: none;
  text-align: center;
}

.um-upload__input {
  display: none;
}
</style>