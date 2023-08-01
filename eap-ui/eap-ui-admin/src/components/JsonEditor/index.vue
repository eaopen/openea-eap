<template>
  <div class="json-editor">
    <textarea ref="textarea" />
  </div>
</template>

<script>
import CodeMirror from 'codemirror'
import './codemirror.css';
import 'codemirror/addon/lint/lint.css'
import 'codemirror/lib/codemirror.css'
import 'codemirror/theme/rubyblue.css'
require('script-loader!jsonlint')
import 'codemirror/mode/javascript/javascript'
import 'codemirror/addon/lint/lint'
import 'codemirror/addon/lint/json-lint'
// 代码提示功能具体语言可以从 codemirror/addon/hint/下引入多个
import 'codemirror/mode/sql/sql';
import 'codemirror/addon/hint/show-hint.css';
import 'codemirror/addon/hint/show-hint';
import 'codemirror/addon/hint/sql-hint';

// 自动括号匹配功能
import 'codemirror/addon/edit/matchbrackets'
import 'codemirror/addon/edit/closebrackets'

export default {
  name: 'JsonEditor',
  /* eslint-disable vue/require-prop-types */
  props: {
    value: {
      default: '',
    },
    mode: {
      type: String,
      default: 'json'
    },
    readOnly: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      jsonEditor: null,
      options: {
        lineNumbers: true,
        mode: this.mode,
        readOnly: this.readOnly,
        tabSize: 2,
        lineWrapping: true,
        styleActiveLine: true, //Line选择是是否加亮
        matchBrackets: true,
        autoCloseBrackets: true, //括号匹配
        extraKeys: { "Ctrl-Space": "autocomplete" },
        hintOptions: {
          keywords: ['SUM', 'SUBTRACT', 'PRODUCT', 'DIVIDE', 'COUNT'],
        }
        // gutters: ['CodeMirror-lint-markers'],
        // theme: 'rubyblue',
        // lint: true
      }
    }
  },
  watch: {
    value(value) {
      const editorValue = this.jsonEditor.getValue()
      if (value !== editorValue) {
        this.jsonEditor.setValue(this.value)
      }
    }
  },
  mounted() {
    this.jsonEditor = CodeMirror.fromTextArea(this.$refs.textarea, this.options)
    var mode = this.jsonEditor.doc.modeOption
    this.jsonEditor.setValue(this.value)
    this.jsonEditor.on('change', cm => {
      this.$emit('changed', cm.getValue())
      this.$emit('input', cm.getValue())
    })
  },
  methods: {
    getValue() {
      return this.jsonEditor.getValue()
    },
    insert(val, hasBrackets) {
      this.jsonEditor.replaceSelection(val)
      this.jsonEditor.focus()
      let pos1 = this.jsonEditor.getCursor()
      let pos2 = {}
      pos2.line = pos1.line
      pos2.ch = hasBrackets ? pos1.ch - 1 : pos1.ch
      this.jsonEditor.setCursor(pos2)
    }
  }
}
</script>

<style scoped>
.json-editor {
  height: 100%;
  position: relative;
}
.json-editor >>> .CodeMirror {
  height: calc(100%);
}
.json-editor >>> .CodeMirror-scroll {
  height: calc(100%);
}
.json-editor >>> .cm-s-rubyblue span.cm-string {
  color: #f08047;
}
</style>
