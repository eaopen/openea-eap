<template>
  <div>
    <input
      v-if="controlType == 1"
      type="text"
      class="form-control"
      v-model="value"
      @change="handler"
    />
    <ab-dict
      v-if="controlType == 2"
      type="text"
      class="input-div"
      v-model="value"
      :dict-key="dictKey"
      v-ab-permission="'w'"
    ></ab-dict>
    <ab-select-async2
	v-if="controlType == 3"
      class="input-div"
      dialog-key="selectDrawingInfo"
      :model="value"
      v-model="value"
      :query-param="{ partsnum: value2 }"
      value-partsname="description"
      value-partsid="id"
	  v-ab-permission="'w'"
    ></ab-select-async2>
    <ab-date
      type="text"
      class="form-control"
      v-if="controlType == 4"
      v-model="value"
      format="yyyy-MM-dd"
    ></ab-date>
  </div>
</template>

<script>
import Vue from "vue";

export default {
  props: ["newValue", "index", "subIndex", "controlType", "dictKey"],
  data: function () {
    return {
	  value: "",
	  value2: ""
    };
  },
  watch: {
    newValue(val) {
      console.log(val);
      this.value = val;
    },
  },
  mounted: function () {
    this.value = this.newValue;
    if (this.dictKey) {
      console.log(this.dictKey);
    }
  },
  methods: {
    handler(val) {
      // 同时传两个INDEX, 用来标识
      console.log(this.value);
      this.$emit(
        "changeval",
        this.value + "|" + this.index + "|" + this.subIndex
      );
    },
  },
};
</script>
