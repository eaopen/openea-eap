const state = {
  visible: false,
  title: '操作',
  width: '30%',
  height: '400px',
  component: '',
  params: {}
};

const mutations = {
  SHOW_DIALOG: (state, visible)=>{
    state.visible = visible
  },
  SET_DIALOG_WIDTH: (state, width)=>{
    state.width = width
  },
  SET_DIALOG_TITLE: (state, title)=>{
    state.title = title
  },
  SET_PARAMS: (state, params)=>{
    state.params = params
  },
  SET_DIALOG_COMPONENT: (state, component)=>{
    state.component = component
  },

};

const actions = {
  showCustomDialog({commit}, data){
    if(data.component) commit("SET_DIALOG_COMPONENT", data.component);
    if(data.width) commit("SET_DIALOG_WIDTH", data.width)
    if(data.params) commit("SET_PARAMS", data.params)
    if(data.title) commit("SET_DIALOG_TITLE", data.title)
    commit("SHOW_DIALOG", true)
  },
  hideDialog({commit}){
    commit('SHOW_DIALOG', false)
  }
};

export default {
  namespaced: true,
  state,
  mutations,
  actions,
};
