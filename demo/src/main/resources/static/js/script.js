// CSRFトークンの設定
const token = $("meta[name='_csrf']").attr("content");
const header = $("meta[name='_csrf_header']").attr("content");

// Ajaxリクエストの共通設定
$.ajaxSetup({
    beforeSend: function(xhr) {
        xhr.setRequestHeader(header, token);
    }
});

// DOM読み込み完了時の処理
$(document).ready(function() {
    // モーダル関連の要素
    const bookModal = $("#bookModal");
    const borrowModal = $("#borrowModal");
    const closeButtons = $(".close");
    
    // モーダルのドラッグ機能
    $(".modal-content").each(function() {
        makeDraggable($(this));
    });

    // 新規登録ボタンのクリックイベント
    $("#newBookBtn").click(function() {
        clearForm();
        $("#modalTitle").text("新規登録");
        bookModal.show();
        centerModal($("#bookModal .modal-content"));
    });

    // モーダルを閉じる
    closeButtons.click(function() {
        $(this).closest('.modal').hide();
    });

    // モーダル外クリックで閉じる
    $(window).click(function(event) {
        if ($(event.target).hasClass('modal')) {
            $('.modal').hide();
        }
    });

    // 書籍情報保存
    $("#bookForm").submit(function(e) {
        e.preventDefault();
        const bookId = $("#bookId").val();
        const bookData = {
            title: $("#title").val(),
            author: $("#author").val(),
            category: $("#category").val(),
            publishDate: $("#publishDate").val(),
            price: parseInt($("#price").val())
        };

        try {
            validateInput(bookData);
        } catch (error) {
            showError(error.message);
            return;
        }

        // 新規登録か更新かを判定
        const url = bookId ? `/api/books/${bookId}` : '/api/books';
        const method = bookId ? 'PUT' : 'POST';

        $.ajax({
            url: url,
            method: method,
            contentType: 'application/json',
            data: JSON.stringify(bookData),
            success: function() {
                location.reload();
            },
            error: function(xhr) {
                showError('エラーが発生しました: ' + xhr.responseText);
            }
        });
    });

    // 編集ボタンのクリックイベント
    $(document).on('click', '.btn-edit', function() {
        const bookId = $(this).data('id');
        console.log('Edit button clicked, bookId:', bookId); // デバッグログ
        
        $.ajax({
            url: `/api/books/${bookId}`,
            method: 'GET',
            success: function(book) {
                console.log('Book data received:', book); // デバッグログ
                $("#bookId").val(book.bookId);
                $("#title").val(book.title);
                $("#author").val(book.author);
                $("#category").val(book.category);
                $("#publishDate").val(formatDate(book.publishDate));
                $("#price").val(book.price);
                $("#modalTitle").text("書籍編集");
                bookModal.show();
                centerModal($("#bookModal .modal-content"));
            },
            error: function(xhr) {
                showError('書籍情報の取得に失敗しました: ' + xhr.responseText);
            }
        });
    });

    // 削除ボタンのクリックイベント
    $(document).on('click', '.btn-delete', function() {
        if (!confirm('本当に削除しますか？')) return;
        
        const bookId = $(this).data('id');
        $.ajax({
            url: `/api/books/${bookId}`,
            method: 'DELETE',
            success: function() {
                location.reload();
            },
            error: function(xhr) {
                showError('削除に失敗しました: ' + xhr.responseText);
            }
        });
    });

    // 貸出ボタンのクリックイベント
    $(document).on('click', '.btn-borrow', function() {
        $("#borrowBookId").val($(this).data('id'));
        borrowModal.show();
        centerModal($("#borrowModal .modal-content"));
    });

    // 貸出処理
    $("#borrowForm").submit(function(e) {
        e.preventDefault();
        const bookId = $("#borrowBookId").val();
        const borrowData = {
            borrower: $("#borrower").val(),
            expectedReturnDate: $("#expectedReturnDate").val()
        };

        $.ajax({
            url: `/api/books/${bookId}/borrow`,
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(borrowData),
            success: function() {
                location.reload();
            },
            error: function(xhr) {
                showError('貸出に失敗しました: ' + xhr.responseText);
            }
        });
    });

    // 返却ボタンのクリックイベント
    $(document).on('click', '.btn-return', function() {
        if (!confirm('返却処理を行いますか？')) return;

        const bookId = $(this).data('id');
        $.ajax({
            url: `/api/books/${bookId}/return`,
            method: 'POST',
            success: function() {
                location.reload();
            },
            error: function(xhr) {
                showError('返却に失敗しました: ' + xhr.responseText);
            }
        });
    });
});

// モーダルを中央に配置
function centerModal($modal) {
    const windowHeight = $(window).height();
    const modalHeight = $modal.height();
    const top = Math.max(windowHeight - modalHeight, 0) / 2;
    $modal.css({
        top: top + 'px'
    });
}

// モーダルをドラッグ可能にする
function makeDraggable($element) {
    let pos1 = 0, pos2 = 0, pos3 = 0, pos4 = 0;
    
    $element.find('h2').css('cursor', 'move').on('mousedown', dragMouseDown);

    function dragMouseDown(e) {
        e.preventDefault();
        pos3 = e.clientX;
        pos4 = e.clientY;
        $(document).on('mousemove', elementDrag);
        $(document).on('mouseup', closeDragElement);
    }

    function elementDrag(e) {
        e.preventDefault();
        pos1 = pos3 - e.clientX;
        pos2 = pos4 - e.clientY;
        pos3 = e.clientX;
        pos4 = e.clientY;
        
        const newTop = $element.offset().top - pos2;
        const newLeft = $element.offset().left - pos1;
        
        $element.css({
            top: newTop + 'px',
            left: newLeft + 'px',
            transform: 'none',
            margin: '0'
        });
    }

    function closeDragElement() {
        $(document).off('mousemove', elementDrag);
        $(document).off('mouseup', closeDragElement);
    }
}

// フォームをクリア
function clearForm() {
    $("#bookId").val('');
    $("#title").val('');
    $("#author").val('');
    $("#category").val('');
    $("#publishDate").val('');
    $("#price").val('');
}

// 日付のフォーマット
function formatDate(date) {
    if (!date) return '';
    const d = new Date(date);
    const year = d.getFullYear();
    const month = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
}

// 価格のフォーマット
function formatPrice(price) {
    return new Intl.NumberFormat('ja-JP').format(price);
}

// エラーメッセージの表示
function showError(message) {
    const errorDiv = $("<div>")
        .addClass("error-message")
        .text(message)
        .appendTo("body");
    
    setTimeout(() => {
        errorDiv.fadeOut(500, function() {
            $(this).remove();
        });
    }, 3000);
}

// 入力値のバリデーション
function validateInput(data) {
    if (!data.title || data.title.trim() === '') {
        throw new Error('タイトルを入力してください');
    }
    if (!data.author || data.author.trim() === '') {
        throw new Error('著者を入力してください');
    }
    if (!data.category || data.category.trim() === '') {
        throw new Error('カテゴリを入力してください');
    }
    if (!data.publishDate) {
        throw new Error('出版日を入力してください');
    }
    if (!data.price || data.price < 0) {
        throw new Error('正しい価格を入力してください');
    }
}
