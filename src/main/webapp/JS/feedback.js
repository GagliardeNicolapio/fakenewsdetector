

window.onbeforeunload = function() {
    document.getElementById('contenitoreProg').style.display = 'block';
    document.getElementById('containerFeedback').style.display = 'none';
    document.getElementsByTagName('header')[0].style.display = 'none';
};


function abortPreloader(){
    window.onbeforeunload = function (){}
}