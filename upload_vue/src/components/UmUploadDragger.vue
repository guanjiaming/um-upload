<template>
  <div
      class="um-upload-dragger"
      :class="{'um-upload__dragover': isDragover}"
      @dragenter.prevent
      @dragover.prevent="isDragover = true;"
      @drop.prevent="onDrop"
      @dragleave.prevent="isDragover = false;"
  >
    <slot></slot>
  </div>
</template>
<script setup>
import {ref} from "vue";
let isDragover = ref(false);

const emit = defineEmits(["file-drop"])

function onDrop(e){
  isDragover.value = false;
  emit("file-drop", e);
}
</script>
<style>
.um-upload-dragger {
  padding: 40px 18px;
  background-color: #fff;
  border: 1px dashed #dcdfe6;
  border-radius: 10px;
  box-sizing: border-box;
  text-align: center;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  font-size: 30px;
}

.um-upload__dragover {
  border: 2px dashed teal;
  background: rgba(0, 128, 128, 0.2);
}

.um-upload-dragger:hover {
  border: 1px dashed teal;
}

.um-upload__dragover:hover {
  border: 2px dashed teal;
}
</style>