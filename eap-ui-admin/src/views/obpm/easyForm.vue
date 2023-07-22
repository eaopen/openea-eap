<template>
     <div>
       <easy-form-creater :formKey="key"/>
    </div>
</template>
<script>
import easyFormCreater from '@/components/obpm/easyForm/form.vue'
export default {
  name: 'easyForm',
  components: { easyFormCreater },
  data() {
    return {
      key: "preProjectBasicEdit"
    }
  },
  methods: {

  },

}
</script>

