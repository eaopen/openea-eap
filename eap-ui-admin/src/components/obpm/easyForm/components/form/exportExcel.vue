<template>
  <el-button  @click="exportExcel" size="mini" type="primary" :loading="btnLoading"  icon="el-icon-download">导出</el-button>
</template>

<script>
import Vue from "vue";
import FileSaver from "file-saver";
import XLSX from "xlsx";

export default {
  name: "exportExcel",
  props: ['id','name','list'],
  data(){
    return {
      btnLoading: false,
      time: null
    }
  },
  beforeDestroy(){
    clearTimeout(this.time)
  },
  methods: {
    exportExcel() {
      /* generate workbook object from table */
      if(this.list && !this.list.length){
        $.Dialog.error("请先搜索到数据，再执行导出")
        return false
      }
      this.btnLoading = true
      this.time = setTimeout(()=>{
        this.btnLoading = false
      }, 1000)
      let wb = XLSX.utils.table_to_book(
        document.querySelector("#" + this.id)
      );
      /* get binary string as output */
      let wbout = XLSX.write(wb, {
        bookType: "xlsx",
        bookSST: true,
        type: "array",
      });
      try {
        FileSaver.saveAs(
          new Blob([wbout], { type: "application/octet-stream" }),
          this.name + ".xlsx"
        );
      } catch (e) {
        if (typeof console !== "undefined") console.log(e, wbout);
      }
      return wbout;
    },
  },
};
</script>

