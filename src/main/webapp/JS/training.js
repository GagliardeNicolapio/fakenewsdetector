var j48Split = document.getElementById("j48Split");
j48Split.addEventListener('change',function (){
    var x = document.getElementById("j48Eval");
    if (x.style.display === "none") {
        x.style.display = "block";
    } else {
        x.style.display = "none";
    }
});

var naiveSplit = document.getElementById("naiveSplit");
naiveSplit.addEventListener('change',function (){
    var x = document.getElementById("NaiveEval");
    if (x.style.display === "none") {
        x.style.display = "block";
    } else {
        x.style.display = "none";
    }
});

var naiveCross = document.getElementById("naiveCross");
naiveCross.addEventListener('change',function (){
    var x = document.getElementById("NaiveKfold");
    if (x.style.display === "none") {
        x.style.display = "block";
    } else {
        x.style.display = "none";
    }
});


var naiveNtimesCV = document.getElementById("naiveNtimesCV");
naiveNtimesCV.addEventListener('change',function (){
    var x = document.getElementById("NaiveNtimes");
    if (x.style.display === "none") {
        x.style.display = "block";
    } else {
        x.style.display = "none";
    }
});