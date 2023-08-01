export const filterHtml = function(str){
  let html =  str.replace(/\u200B/g, '');
  let custScriptStr = "",customScript
  if(html.indexOf("<script>") != -1){
    const reg = /<script[^>]*?>[\s\S]*?<\/script>/i;
		let patten = reg.exec(html)
		let script = patten[0];

    var len1 = script.indexOf(">")
		var len2 = script.lastIndexOf("<")
		custScriptStr = script.substring(len1 + 1, len2);
    if(custScriptStr){
      customScript = eval(custScriptStr.substring(custScriptStr.indexOf("window.custFormComponentMixin")))
    }
		html = html.replace(reg, "");
    customScript.created = ()=>{}
  }
  return {
    html,
    customScript
  }
}

export const generateUUID = function() {
	let d = new Date().getTime();
	if (window.performance && typeof window.performance.now === "function") {
		d += performance.now(); //use high-precision timer if available
	}
	let uuid = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
		let r = (d + Math.random() * 16) % 16 | 0;
		d = Math.floor(d / 16);
		return (c == 'x' ? r : (r & 0x3 | 0x8)).toString(16);
	});
	return uuid.replaceAll('-','');
}
