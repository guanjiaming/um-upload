<template>
  <h1> 文件上传 </h1>
  <h2> 演示点击上传 </h2>
  <h2> 演示拖拽上传 </h2>
  <h2> 演示秒上传 </h2>
  <um-upload
      drag
      action="upload/file"
      multiple
      second-upload="upload/verify"
      :on-success="onSuccess"
  >
    <svg class="icon" style="width: 2em;height: 2em;vertical-align: middle;fill: currentColor;overflow: hidden;"
         viewBox="0 0 1024 1024" xmlns="http://www.w3.org/2000/svg">
      <path
          d="M770.354 352.086C721.58 240.25 610.168 162.002 480.364 162.002 305.644 162.002 164 303.642 164 478.364c0 0.954 0.134 1.874 0.142 2.824C74.332 496.22 5.818 574.102 5.818 668.182 5.818 773.016 90.804 858 195.638 858h601.088v-2.186c124.814-15.598 221.458-121.862 221.458-250.906 0-137.998-110.506-249.994-247.83-252.822zM575.272 510.002v221.454H448.726V510.002H353.82L512 351.82l158.182 158.182h-94.91z"></path>
    </svg>
    <div class="um-upload__text">
      <span>拖拽文件到这里</span>
      <span class="color-link">点击上传</span>
    </div>
  </um-upload>
</template>

<script setup>
import UmUpload from "./components/UmUpload.vue";

defineProps({
  msg: String
})

function onSuccess(res) {
  console.log("自己的success", res);
}

</script>


<style>
h1, h2 {
 text-align: center;
}

.icon {
  color: teal;
  pointer-events: none;
}

.um-upload__text {
  pointer-events: none;
  user-select: none;
  font-size: 14px;
}

.color-link {
  color: teal;
}
</style>
