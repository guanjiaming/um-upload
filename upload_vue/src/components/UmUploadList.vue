<template>
  <ol class="um-upload__list">
    <li class="um-upload__list-item" v-for="file in fileList">
      <a class="um-upload__list-item-link">{{ file.name }}</a>
      <!--进度条-->
      <div class="um-upload__progress" v-if="file.status === 'uploading'">
        <span class="um-upload__progress-item" :style="{width: `${file.percent}%`}"></span>
        <span class="um-upload__progress-text"></span>
      </div>
    </li>
  </ol>
</template>
<script setup>
defineProps({
  fileList: {
    type: Array
  }
})
</script>
<style>
.um-upload__list-item {
  position: relative;
  margin: 10px 0;
  text-align: left;
}

.um-upload__list-item-link {
  display: block;
  color: #333;
  font-size: 12px;
}

.um-upload__list-item-link:hover {
  color: teal;
}

.um-upload__progress {
  position: absolute;
  z-index: 1000;
  pointer-events: none;
  height: 4px;
  width: 100%;
  left: 0;
  right: 0;
  bottom: -8px;
  margin-top: -2px;
  background: rgba(0, 128, 128, .2);
  border-radius: 8px;
  opacity: 0.8;
}

.um-upload__progress-item {
  background: teal;
  position: absolute;
  top: 0;
  left: 0;
  bottom: 0;
  transition: width 300ms ease-in-out;
}
</style>