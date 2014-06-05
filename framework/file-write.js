// container - select highly positioned container
var container = document.querySelector('#file_export_container');

var typer = container.querySelector('[contenteditable]');
var output = container.querySelector('output');

const MIME_TYPE = 'text/plain';

// Rockstars use event delegation!
// event binding - drag, keydown
document.body.addEventListener('dragstart', function (e) {
    var a = e.target;
    if (a.classList.contains('dragout')) {
        e.dataTransfer.setData('DownloadURL', a.dataset.downloadurl);
    }
}, false);

document.body.addEventListener('dragend', function (e) {
    var a = e.target;
    if (a.classList.contains('dragout')) {
        cleanUp(a);
    }
}, false);

document.addEventListener('keydown', function (e) {
    if (e.keyCode == 27) {  // Esc
        document.querySelector('details').open = false;
    } else if (e.shiftKey && e.keyCode == 191) { // shift + ?
        document.querySelector('details').open = true;
    }
}, false);

// file write - only chrome
var cleanUp = function (a) {
    a.textContent = 'Downloaded';
    a.dataset.disabled = true;

    // Need a small delay for the revokeObjectURL to work properly.
    setTimeout(function () {
        window.URL.revokeObjectURL(a.href);
    }, 1500);
};

// 가장 최근부분만 일단 저장한다.
var downloadFile = function () {
    window.URL = window.webkitURL || window.URL;

    var prevLink = output.querySelector('a');
    if (prevLink) {
        window.URL.revokeObjectURL(prevLink.href);
        output.innerHTML = '';
    }
    // typer.textContent - div[contenteditable] 의 내용이다.
    // 가장 최근에 저장한 부분만 일단 저장한다.

    var bb /* = new Blob([typer.textContent], {type: MIME_TYPE});*/
    if (tmpProjectContainer) {
        bb = new Blob([JSON.stringify(tmpProjectContainer)], {type: MIME_TYPE});
    } else {
        // 빈 개체인 경우
        bb = new Blob([typer.textContent], {type: MIME_TYPE});
        alert("export File Error: 파일이 없습니다.");
    }

    var a = document.createElement('a');
    a.download = container.querySelector('input[type="text"]').value;
    a.href = window.URL.createObjectURL(bb);
    a.textContent = 'Download ready';

    a.dataset.downloadurl = [MIME_TYPE, a.download, a.href].join(':');
    a.draggable = true; // Don't really need, but good practice.
    a.classList.add('dragout');

    output.appendChild(a);

    a.onclick = function (e) {
        if ('disabled' in this.dataset) {
            return false;
        }

        cleanUp(this);
    };
};/**
 * Created by KIMSEONHO on 14. 2. 12.
 */
