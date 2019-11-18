let score = 0;
let timesGuessedWrong = 0;
let guessedRightArray = [];
let keepLooping = true;
let indexWord = -1;
let correctAnswerField;
let startTime;
let endTime;
let wordCN;
let wordsChinese = [];
let typeExercise = readCookie("typingExercise");
reading = false;
timer = false;
let currentWord;
let arrayWithWriters = [];
let TimeQuizStart;
let counterBack;

function lessonWriter(word) {
    this.HanziWriter.create(id, word, {
        width: 115,
        height: 115,
        padding: 2,
        strokeAnimationSpeed: 2,
        delayBetweenStrokes: 20,
    });
}


function readCookie(name) {
    var nameEQ = name + "=";
    var ca = document.cookie.split(';');
    for (var i = 0; i < ca.length; i++) {
        var c = ca[i];
        while (c.charAt(0) == ' ') c = c.substring(1, c.length);
        if (c.indexOf(nameEQ) == 0) return c.substring(nameEQ.length, c.length);
    }
    return null;
}

function createCookie(name, value, days) {
    if (days) {
        var date = new Date();
        date.setTime(date.getTime() + (days * 24 * 60 * 60 * 1000));
        var expires = "; expires=" + date.toGMTString();
    }
    else var expires = "";
    document.cookie = name + "=" + value + expires + "; path=/";
}

function eraseCookie(name) {
    createCookie(name, "", -1);
}


$(document).ready(function () {


    if (Cookies.get('typingExercise') === "true") {
        $("#typing-exercise").attr('checked', 'checked');
    }

    if (Cookies.get('darkTheme') === "true") {
        $(".theme-btn").addClass("active");
        document.body.style.backgroundColor = "#401511";
        $('h3,div,th,label').each(function (i, ele) {
            ele.style.color = "#ffffff";


        });
        $('tr,td,table').each(function (i, ele) {
            ele.style.color = "#000000";
        });


        $('head').append('<style>  hr{background-color:white}/>');

        $("button").removeClass("btn-outline-primary");
    }

    $("#typing-exercise").click(function () {
        let darkTheme = readCookie("typingExercise");
        if (darkTheme) {
            eraseCookie("typingExercise");
            location.reload();
        }
        else {
            createCookie("typingExercise", true, 365);
            location.reload();
        }
    });

    $("#themeChanger").click(function () {
        var darkTheme = readCookie("darkTheme");
        if (darkTheme) {
            eraseCookie("darkTheme");
            location.reload();
        }
        else {
            createCookie("darkTheme", true, 365);
            location.reload();
        }
    });


    $(document).ready(function () {
        //store all words with their 'writers' so we can play the animation on click
        let arrWithWriters = new Map();
        $(".wordLessonDraw").each(function (i, obj) {

            let id = $(this).attr("id");
            let elementArr = $("#" + id).attr("data-thing");
            let idArr = id.split("-");
            let buttonId = "button-" + idArr[1];
            arrayWithWriters = [];
            let cnWord = elementArr.split('');
            for (let i = 0; i < cnWord.length; i++) {
                arrayWithWriters[i] = HanziWriter.create(id, cnWord[i], {
                    width: 115,
                    height: 115,
                    padding: 2,
                    strokeAnimationSpeed: 2,
                    delayBetweenStrokes: 20,
                });
            }
            //set Array with Writers for each word
            arrWithWriters.set(id, arrayWithWriters);

            document.getElementById(buttonId).addEventListener('click', function () {
                //get clicked button id (see which element was clicked)
                let buttonIdArr = $(this).attr("id").split("-");
                //get the writers for the element
                let writerId = "character-" + buttonIdArr[1];
                let writers = arrWithWriters.get(writerId);
                //animate them with delay between words
                for (let i = 0; i < writers.length; i++) {
                    setTimeout(function () {
                        writers[i].animateCharacter();
                    }, i * 2250);
                }
            });


        });


        if (window.location.href.indexOf("register") > -1) {
            //if the user clicks register it will show the register form instead of the login one
            $("#tab-2").click();
        }

        //for showing the popup with the meaning of "type mode" in the footer
        let modal = document.getElementById('myModal');
        let btn = document.getElementById("myBtn");
        let span = document.getElementsByClassName("close")[0];
        if (btn !== null && modal !== null && span !== null) {
            btn.onclick = function () {
                modal.style.display = "block";
            };
            span.onclick = function () {
                modal.style.display = "none";
            };
            window.onclick = function (event) {
                if (event.target === modal) {
                    modal.style.display = "none";
                }
            };
        }


        $("#addList").on("click", function (event) {
            //get the information from admin and convert it to json
            //for creating list/lesson
            let title = document.getElementById("listTitle").value;
            let lang = document.getElementById("listLang").value;
            let lessonText = document.getElementById("lessonText").value;
            let table = $('#table_list_words').DataTable();
            let selected = table.rows('.selected').data();
            let wordsToAdd = [];
            for (let i = 0; i < selected.length; i++) {
                wordsToAdd.push(selected[i][0])
            }
            let listDetails = {
                title: title,
                selected: wordsToAdd,
                listLang: lang,
                lessonText: lessonText,
            };
            $.ajax({
                type: "POST",
                contentType: "application/json",
                url: "/createList",
                data: JSON.stringify(listDetails),
                dataType: 'json',
            });
            event.preventDefault();
            location.reload();
        });

        let clickQuiz = document.getElementById("clickQuiz");
        if (typeof(clickQuiz) != 'undefined' && clickQuiz != null) {
            clickQuiz.click();
            //Control quiz with KEYBOARD without mouse
            window.addEventListener("keydown", function (event) {
                if (event.defaultPrevented) {
                    return;
                }
                switch (event.key) {
                    case "R":
                    case "r":

                        textToSpeech(wordCN);

                        break;
                    case "1":
                        $("#choice1").click();
                        break;
                    case "2":
                        $("#choice2").click();
                        break;
                    case "3":
                        $("#choice3").click();
                        break;
                    case "4":
                        $("#choice4").click();
                        break;
                    case "Enter":
                        $("#submitWord").click();
                        break;
                    default:
                        return;
                }
                if (Cookies.get('typingExercise') === "false") {
                    event.preventDefault();
                }
            }, true);
        }
        //Initialize the dataTables and their settings
        if ($("#leaderboard") !== null) {
            $("#leaderboard").DataTable({
                //set place as only 10% width
                "columnDefs": [
                    {"width": "10%", "targets": 0}
                ],
                searching: true,
                select: false,
                responsive: true,
                scrollX: false,
                scrollY: true,
                "language": {
                    "sProcessing": "Обработка на резултатите...",
                    "sLengthMenu": "Показване на _MENU_ резултата",
                    "sZeroRecords": "Няма намерени резултати",
                    "sInfo": "Показване на резултати от _START_ до _END_ от общо _TOTAL_",
                    "sInfoEmpty": "Показване на резултати от 0 до 0 от общо 0",
                    "sInfoFiltered": "(филтрирани от общо _MAX_ резултата)",
                    "sInfoPostFix": "",
                    "sSearch": "Търсене:",
                    "sUrl": "",
                    "oPaginate": {
                        "sFirst": "Първа",
                        "sPrevious": "Предишна",
                        "sNext": "Следваща",
                        "sLast": "Последна"
                    }
                }
            });
        }


        $('#table_id').DataTable({
            searching: true,
            responsive: true,
            scrollX: false,
            scrollY: true,
            "language": {
                "sProcessing": "Обработка на резултатите...",
                "sLengthMenu": "Показване на _MENU_ резултата",
                "sZeroRecords": "Няма намерени резултати",
                "sInfo": "Показване на резултати от _START_ до _END_ от общо _TOTAL_",
                "sInfoEmpty": "Показване на резултати от 0 до 0 от общо 0",
                "sInfoFiltered": "(филтрирани от общо _MAX_ резултата)",
                "sInfoPostFix": "",
                "sSearch": "Търсене:",
                "sUrl": "",
                "oPaginate": {
                    "sFirst": "Първа",
                    "sPrevious": "Предишна",
                    "sNext": "Следваща",
                    "sLast": "Последна"
                }
            }

        });
        $('#table_category').DataTable({
            searching: true,
            responsive: true,
            scrollX: false,
            scrollY: true,
            "language": {
                "sProcessing": "Обработка на резултатите...",
                "sLengthMenu": "Показване на _MENU_ резултата",
                "sZeroRecords": "Няма намерени резултати",
                "sInfo": "Показване на резултати от _START_ до _END_ от общо _TOTAL_",
                "sInfoEmpty": "Показване на резултати от 0 до 0 от общо 0",
                "sInfoFiltered": "(филтрирани от общо _MAX_ резултата)",
                "sInfoPostFix": "",
                "sSearch": "Търсене:",
                "sUrl": "",
                "oPaginate": {
                    "sFirst": "Първа",
                    "sPrevious": "Предишна",
                    "sNext": "Следваща",
                    "sLast": "Последна"
                }
            }
        });
        $('#table_list_words').DataTable({

            searching: true,
            responsive: true,
            scrollX: false,
            scrollY: true,
            select: {
                style: 'multi'
            },
            "language": {
                "sProcessing": "Обработка на резултатите...",
                "sLengthMenu": "Показване на _MENU_ резултата",
                "sZeroRecords": "Няма намерени резултати",
                "sInfo": "Показване на резултати от _START_ до _END_ от общо _TOTAL_",
                "sInfoEmpty": "Показване на резултати от 0 до 0 от общо 0",
                "sInfoFiltered": "(филтрирани от общо _MAX_ резултата)",
                "sInfoPostFix": "",
                "sSearch": "Търсене:",
                "sUrl": "",
                "oPaginate": {
                    "sFirst": "Първа",
                    "sPrevious": "Предишна",
                    "sNext": "Следваща",
                    "sLast": "Последна"
                }
            }
        })
    });
});


function correctAnswer(word, correctWordIndex) {
    score += 1;
    guessedRightArray[correctWordIndex] = true;
    $(correctAnswerField).addClass("btn-success");
    $(correctAnswerField).removeClass("btn-light");
    $("#wordGuessed").addClass("green-border");
    if (!guessedRightArray.includes(false)) {
        endTime = (performance.now() - startTime);
        let data = {
            score: score,
            time: endTime,
            timesWrong: timesGuessedWrong
        };


        //If less than 3 times wrong then the words are learned
        if (timesGuessedWrong < 3) {
            $.ajax({
                type: "POST",
                contentType: "application/json",
                url: window.location.href + "/learnedWords",
                data: JSON.stringify(wordsChinese),
                dataType: 'json',
                async: false,
            });
            //send data to server to get XP
            $.ajax({
                type: "POST",
                contentType: "application/json",
                url: window.location.href + "/quizDone",
                data: JSON.stringify(data),
                dataType: 'json',
            });

        }
        else {
            //send data to server to get XP but not learned words because more than 3 mistakes
            $.ajax({
                type: "POST",
                contentType: "application/json",
                url: window.location.href + "/quizDone",
                data: JSON.stringify(data),
                dataType: 'json',
            });

        }

        window.location.href = window.location.href + "/quizDone?timesWrong=" + timesGuessedWrong + "&time=" + parseInt(endTime / 1000, 10);

    }
    else {
        keepLooping = true;
        let millisecondsToWait = 1000;
        //set timeout for the user to see the answer
        setTimeout(function () {
            if (typeExercise) {
                $("#wordGuessed").addClass("green-border");
                quizType(word);
            }
            else {
                quiz(word);
            }
        }, millisecondsToWait);
    }
}

function incorrectAnswer(word, e) {
    keepLooping = true;
    timesGuessedWrong += 1;
    let millisecondsToWait = 1000;
    TimeQuizStart = performance.now();
    $("#wordGuessed").addClass("red-border");
    if (typeExercise) {
        setTimeout(function () {
            $("#wordGuessed").addClass("red-border");
            quizType(word);
        }, millisecondsToWait);
    }
    else {
        if (e !== null) {
            let elementClicked = e.target;
            $(elementClicked).addClass("btn-danger");
            $(elementClicked).removeClass("btn-light");
        }
        $(correctAnswerField).addClass("btn-success");
        $(correctAnswerField).removeClass("btn-light");

        setTimeout(function () {

            quiz(word);
        }, millisecondsToWait);
    }
}


function quizType(word) {
    //THIS IS THE TYPING QUIZ
    let data = JSON.parse(word);
    let lengthArray = data.length;
    //create the timer
    TimeProgressBar();
    document.getElementById("wordGuessed").value = "";
    $("#wordGuessed").removeAttr('class');
    $("#wordGuessed").addClass('form-control small-input');
    if (score === 0 && timesGuessedWrong === 0) {
        startTime = performance.now();
        for (let i = 0; i < lengthArray; i++) {
            guessedRightArray.push(false);
            wordsChinese.push(data[i]["id"])
        }
    }


    while (guessedRightArray.includes(false) && keepLooping === true) {
        let tempData = data;
        //Delete previous answers
        $("#wordToGuess").html("");

        indexWord = getRandomNumberIndexInArray(getAllIndexes(guessedRightArray, false));
        let wordToGuess = data[indexWord];
        if (indexWord > -1) {
            tempData.splice(indexWord, 1);
        }

        //for displaying the word and its pronunciation
        $("#wordToGuess").append("<span class='wordToGuess' style='vertical-align: middle'>" + wordToGuess["bg_meaning"] + "</span>");
        $("#wordToGuess").append("<img width='50' height='50' style='vertical-align: middle' src='/img/playIcon.png' onclick='textToSpeech(wordCN)'/>");

        currentWord = wordToGuess;
        $('#submitWord').unbind().click(function () {
            let guessedWord = document.getElementById("wordGuessed").value;
            if (guessedWord === wordToGuess["word"]) { //if user answered correctly
                correctAnswer(word, indexWord);
            }
            else {
                incorrectAnswer(word, "");
            }
        });

        this.timerQuiz(performance.now(), word, score, timesGuessedWrong);
        keepLooping = false;
        textToSpeech(wordToGuess["word"]);
        wordCN = wordToGuess["word"];
    }
}


function quiz(word) {
    //THIS IS THE CHOOSING QUIZ
    let data = JSON.parse(word);
    let lengthArray = data.length;

    //create the timer
    TimeProgressBar();

    if (score === 0 && timesGuessedWrong === 0) {
        startTime = performance.now();
        for (let i = 0; i < lengthArray; i++) {
            guessedRightArray.push(false);
            wordsChinese.push(data[i]["id"])
        }
    }

    while (guessedRightArray.includes(false) && keepLooping === true) {

        let tempData = data;
        //Delete previous answers
        $("#wordToGuess").html("");
        $("#choice1").removeAttr('class');
        $("#choice2").removeAttr('class');
        $("#choice3").removeAttr('class');
        $("#choice4").removeAttr('class');

        $(":button").addClass("btn btn-light choicesWords mt-3");

        for (let i = 1; i <= 4; i++) {
            let elemId = "choice" + i;
            let old_element = document.getElementById(elemId);
            let new_element = old_element.cloneNode(true);
            old_element.parentNode.replaceChild(new_element, old_element);
        }

        //get random word and its index
        indexWord = getRandomNumberIndexInArray(getAllIndexes(guessedRightArray, false));
        let wordToGuess = data[indexWord];

        if (indexWord > -1) {
            tempData.splice(indexWord, 1);
        }

        //get correct answer index
        let correctAnswerIndex = getRandomNumberBetween1And4();
        let fieldNameAnswer = "#choice" + correctAnswerIndex;
        correctAnswerField = fieldNameAnswer;
        //add the word for guessing in the div
        $("#wordToGuess").append("<h3>" + wordToGuess["pronunciation"] + "<img  src='/img/playIcon.png' onclick='textToSpeech(wordCN)'/>" + "</h3>");

        arrayWithWriters = [];
        let cnWord = wordToGuess["word"].split('');
        for (let i = 0; i < cnWord.length; i++) {
            //create writers for visualizing the stroke order
            arrayWithWriters[i] = this.HanziWriter.create('wordToGuess', cnWord[i], {
                width: 115,
                height: 115,
                padding: 2,
                strokeAnimationSpeed: 2,
                delayBetweenStrokes: 20,
            });
        }
        document.getElementById('animate-button').addEventListener('click', function () {
            for (let i = 0; i < arrayWithWriters.length; i++) {
                setTimeout(function () {
                    arrayWithWriters[i].animateCharacter();
                }, i * 2250);
            }
        });

        //Shuffle the data
        tempData = tempData.sort(function () {
            return 0.5 - Math.random()
        });

        let answer1 = tempData[0];
        let answer2 = tempData[1];
        let answer3 = tempData[2];

        switch (correctAnswerIndex) {
            //add properties to the buttons and on the correct one add the function for correct answer
            case 1:
                $("#choice1").prop('value', wordToGuess["bg_meaning"]);
                document.getElementById('choice1').addEventListener("click", function (event) {
                    correctAnswer(word, indexWord);
                    event.preventDefault();
                });

                $("#choice2").addClass("incr");
                $("#choice3").addClass("incr");
                $("#choice4").addClass("incr");

                $("#choice2").prop('value', answer1["bg_meaning"]);
                $("#choice3").prop('value', answer2["bg_meaning"]);
                $("#choice4").prop('value', answer3["bg_meaning"]);

                break;
            case 2:
                $("#choice1").prop('value', answer1["bg_meaning"]);
                document.getElementById('choice2').addEventListener("click", function (event) {
                    correctAnswer(word, indexWord);
                    event.preventDefault();
                });
                $("#choice2").prop('value', wordToGuess["bg_meaning"]);
                $("#choice3").prop('value', answer2["bg_meaning"]);
                $("#choice4").prop('value', answer3["bg_meaning"]);


                $("#choice1").addClass("incr");
                $("#choice3").addClass("incr");
                $("#choice4").addClass("incr");

                break;
            case 3:


                $("#choice1").prop('value', answer1["bg_meaning"]);
                $("#choice2").prop('value', answer2["bg_meaning"]);
                document.getElementById('choice3').addEventListener("click", function (event) {
                    correctAnswer(word, indexWord);
                    event.preventDefault();
                });
                $("#choice3").prop('value', wordToGuess["bg_meaning"]);
                $("#choice4").prop('value', answer3["bg_meaning"]);


                $("#choice1").addClass("incr");
                $("#choice2").addClass("incr");
                $("#choice4").addClass("incr");

                break;
            case 4:
                $("#choice1").prop('value', answer1["bg_meaning"]);
                $("#choice2").prop('value', answer2["bg_meaning"]);
                document.getElementById('choice4').addEventListener("click", function (event) {
                    correctAnswer(word, indexWord);
                    event.preventDefault();
                });
                $("#choice3").prop('value', answer3["bg_meaning"]);
                $("#choice4").prop('value', wordToGuess["bg_meaning"]);

                $("#choice1").addClass("incr");
                $("#choice2").addClass("incr");
                $("#choice3").addClass("incr");


                break;
        }

        //if the button has class incr the answer was incorrect
        $(".incr").on("click", function (event) {
            incorrectAnswer(word, event);
            event.preventDefault();
        });

        //reset the timer
        this.timerQuiz(performance.now(), word, score, timesGuessedWrong);
        keepLooping = false;
        textToSpeech(wordToGuess["word"]);
        wordCN = wordToGuess["word"];
    }
}


function timerQuiz(timeOnStart, data, scoreQuiz, timesWrong) {
    //create the timer
    let scoreNow = scoreQuiz;
    let timesWrongQuiz = timesWrong;
    setTimeout(function () {
        //check to see if the timer is on the same word or the user guessed correctly
        if ((performance.now() - timeOnStart >= 10) && (scoreNow === score && timesWrongQuiz === timesGuessedWrong) && keepLooping === false) {
            incorrectAnswer(data, null);
        }
    }, 10000);
}


function getAllIndexes(arr, val) {
    let indexes = [], i = -1;
    while ((i = arr.indexOf(val, i + 1)) != -1) {
        indexes.push(i);
    }
    return indexes;
}

function getRandomNumberIndexInArray(lengthArray) {
    let number = Math.floor(Math.random() * lengthArray.length);
    return lengthArray[number];
}

function getRandomNumberBetween1And4() {
    return Math.floor(Math.random() * 4) + 1;
}

function textToSpeech(message) {
    if ('speechSynthesis' in window) {
        let text = message;
        let msg = new SpeechSynthesisUtterance();
        msg.rate = 10;
        msg.rate = 0.7;
        let voices = window.speechSynthesis.getVoices();
        msg.lang = "zh-CN";
        msg.pitch = 1;
        msg.text = text;
        speechSynthesis.speak(msg);
        reading = false;

    } else {
        $('#modal1').show();
    }
}


function TimeProgressBar() {
    //THAT IS THE TIMER FOR THE QUIZ
    let bar = $('.progress-bar');
    //reset the progress bar at 100% without slow animations then activate the animations again
    bar.css('width', '100%');
    $(".progress-bar").addClass("fastProgressBar");
    $(".progress-bar").removeClass("progress-bar-animated");
    bar.css('width', '100%');
    clearInterval(counterBack);
    bar.removeClass("bg-warning");
    bar.removeClass("bg-danger");
    $(".progress-bar").removeClass("fastProgressBar");
    $(".progress-bar").addClass("progress-bar-animated");
    bar.addClass("bg-success");
    let i = 100;
    counterBack = setInterval(function () {
        i--;
        if (i >= 0) {
            bar.css('width', i + '%');
            if (i >= 70 && i <= 100) {
                bar.addClass("bg-success");
            }
            if (i >= 40 && i <= 60) {

                bar.removeClass("bg-success");
                bar.addClass("bg-warning");
            }
            if (i <= 30) {
                bar.removeClass("bg-warning");
                bar.addClass("bg-danger");
            }
        } else {
            bar.css('width', '100%');
            $(".progress-bar").removeClass("progress-bar-animated");
            i = 100;
            clearInterval(counterBack);
        }

    }, 100);

}
