function getExcel() {
  function s2ab(s) {
    if(typeof ArrayBuffer !== 'undefined') {
      var buf = new ArrayBuffer(s.length);
      var view = new Uint8Array(buf);
      for (var i=0; i!=s.length; ++i) view[i] = s.charCodeAt(i) & 0xFF;
      return buf;
    } else {
      var buf = new Array(s.length);
      for (var i=0; i!=s.length; ++i) buf[i] = s.charCodeAt(i) & 0xFF;
      return buf; } }
  function export_table_to_excel(id, type, fn) {
    var wb = XLSX.utils.table_to_book(document.getElementById(id), {sheet:"Sheet JS"});
    var wbout = XLSX.write(wb, {bookType:type, bookSST:true, type: 'binary'});
    var fname = fn || 'test.' + type;
    try {
      saveAs(new Blob([s2ab(wbout)],{type:"application/octet-stream"}), fname);
      } catch(e) { if(typeof console != 'undefined') console.log(e, wbout); }
    return wbout;
  }
  var wbout = export_table_to_excel('mytable', 'xlsx', 'counts.xlsx')
}
$(document).ready(function(){
  $("#xlsxbutton").click(function(){ getExcel(); });
});
