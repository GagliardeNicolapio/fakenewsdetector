function startPreloader(){
    //document.write(' <div id="contenitoreProg"> <div class="h-100 row align-items-center"> <div id="wrapper"> <div>Stiamo analizzando la news. Attendi...</div> <div> <svg> <circle cx="50" cy="50" r="40" stroke="red" stroke-dasharray="78.5 235.5" stroke-width="3" fill="none" /> <circle cx="50" cy="50" r="30" stroke="blue" stroke-dasharray="62.8 188.8" stroke-width="3" fill="none" /> <circle cx="50" cy="50" r="20" stroke="green" stroke-dasharray="47.1 141.3" stroke-width="3" fill="none" /> </svg> </div> </div> </div> </div>');
    document.getElementsByTagName('header')[0].style.display = 'none';
    document.getElementById('contenitoreProg').style.display = 'block';
    document.getElementsByClassName('mainContent')[0].style.display = 'none';
}