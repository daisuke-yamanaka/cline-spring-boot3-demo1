// ページ管理
document.addEventListener('DOMContentLoaded', () => {
    initializeNavigation();
    initializeCharts();
    setupEventListeners();
    loadDashboardData();
    initializeBorrowedFilters(); // 追加：貸出履歴フィルターの初期化
});

// ナビゲーション制御
function initializeNavigation() {
    const navLinks = document.querySelectorAll('.nav-links a');
    navLinks.forEach(link => {
        link.addEventListener('click', (e) => {
            e.preventDefault();
            const targetPage = link.dataset.page;
            showPage(targetPage);
            updateActiveLink(link);
        });
    });
}

// 追加：URLパラメータから値を取得する関数
function getUrlParameter(name) {
    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get(name);
}

// 追加：貸出履歴フィルターの初期化と設定
function initializeBorrowedFilters() {
    const statusFilter = document.getElementById('statusFilter');
    const periodFilter = document.getElementById('periodFilter');
    const searchForm = document.querySelector('.search-form');

    if (statusFilter) {
        // URLパラメータまたはdata-selected属性から状態の値を取得
        const selectedStatus = getUrlParameter('status') || statusFilter.getAttribute('data-selected') || 'all';
        const statusOption = statusFilter.querySelector(`option[value="${selectedStatus}"]`);
        if (statusOption) {
            statusOption.selected = true;
        }

        // フィルター変更時のイベントリスナーを追加
        statusFilter.addEventListener('change', () => {
            // 現在の検索キーワードとページネーションの値を保持
            const keyword = getUrlParameter('keyword') || '';
            const page = getUrlParameter('page') || '1';
            
            // フォームのaction URLを更新
            const baseUrl = searchForm.getAttribute('action');
            const newUrl = `${baseUrl}?status=${statusFilter.value}&period=${periodFilter.value}&keyword=${keyword}&page=${page}`;
            searchForm.setAttribute('action', newUrl);
            
            searchForm.submit();
        });
    }

    if (periodFilter) {
        // URLパラメータまたはdata-selected属性から期間の値を取得
        const selectedPeriod = getUrlParameter('period') || periodFilter.getAttribute('data-selected') || 'all';
        const periodOption = periodFilter.querySelector(`option[value="${selectedPeriod}"]`);
        if (periodOption) {
            periodOption.selected = true;
        }

        // フィルター変更時のイベントリスナーを追加
        periodFilter.addEventListener('change', () => {
            // 現在の検索キーワードとページネーションの値を保持
            const keyword = getUrlParameter('keyword') || '';
            const page = getUrlParameter('page') || '1';
            
            // フォームのaction URLを更新
            const baseUrl = searchForm.getAttribute('action');
            const newUrl = `${baseUrl}?status=${statusFilter.value}&period=${periodFilter.value}&keyword=${keyword}&page=${page}`;
            searchForm.setAttribute('action', newUrl);
            
            searchForm.submit();
        });
    }

    // フォーム送信時の処理を追加
    if (searchForm) {
        searchForm.addEventListener('submit', (e) => {
            e.preventDefault();
            
            // 現在の検索キーワードとページネーションの値を取得
            const keyword = searchForm.querySelector('input[name="keyword"]').value || '';
            const page = getUrlParameter('page') || '1';
            
            // フォームのaction URLを更新
            const baseUrl = searchForm.getAttribute('action');
            const newUrl = `${baseUrl}?status=${statusFilter.value}&period=${periodFilter.value}&keyword=${keyword}&page=${page}`;
            searchForm.setAttribute('action', newUrl);
            
            // フォームを送信
            searchForm.submit();
        });
    }
}

function showPage(pageId) {
    const pages = document.querySelectorAll('.page');
    pages.forEach(page => page.classList.remove('active'));
    document.getElementById(pageId).classList.add('active');
}

function updateActiveLink(activeLink) {
    const navLinks = document.querySelectorAll('.nav-links a');
    navLinks.forEach(link => link.classList.remove('active'));
    activeLink.classList.add('active');
}

// イベントリスナーの設定
function setupEventListeners() {
    // 書籍追加ボタン
    document.getElementById('addBookBtn')?.addEventListener('click', () => {
        showModal('書籍登録', createBookForm());
    });

    // ユーザー追加ボタン
    document.getElementById('addUserBtn')?.addEventListener('click', () => {
        showModal('ユーザー登録', createUserForm());
    });

    // 貸出登録ボタン
    document.getElementById('newBorrowingBtn')?.addEventListener('click', () => {
        showModal('貸出登録', createBorrowingForm());
    });

    // 検索機能
    document.querySelectorAll('.search input').forEach(input => {
        input.addEventListener('input', debounce((e) => {
            const searchTerm = e.target.value;
            const section = e.target.closest('section').id;
            handleSearch(section, searchTerm);
        }, 300));
    });

    // モーダルを閉じる
    document.querySelector('.modal .close')?.addEventListener('click', closeModal);
}

// モーダル管理
function showModal(title, content) {
    const modal = document.getElementById('modal');
    const modalTitle = document.getElementById('modalTitle');
    const modalBody = document.getElementById('modalBody');

    modalTitle.textContent = title;
    modalBody.innerHTML = content;
    modal.style.display = 'block';

    // フォームイベントリスナーの設定
    const form = modalBody.querySelector('form');
    if (form) {
        form.addEventListener('submit', handleFormSubmit);
    }
}

function closeModal() {
    const modal = document.getElementById('modal');
    modal.style.display = 'none';
}

// フォーム生成
function createBookForm(book = null) {
    return `
        <form id="bookForm">
            <div class="form-group">
                <label for="isbn">ISBN</label>
                <input type="text" id="isbn" name="isbn" required value="${book?.isbn || ''}">
            </div>
            <div class="form-group">
                <label for="title">タイトル</label>
                <input type="text" id="title" name="title" required value="${book?.title || ''}">
            </div>
            <div class="form-group">
                <label for="author">著者</label>
                <input type="text" id="author" name="author" required value="${book?.author || ''}">
            </div>
            <div class="form-group">
                <label for="category">カテゴリ</label>
                <select id="category" name="category" required>
                    <option value="">選択してください</option>
                    <option value="文学">文学</option>
                    <option value="科学">科学</option>
                    <option value="歴史">歴史</option>
                    <option value="技術">技術</option>
                </select>
            </div>
            <button type="submit" class="btn primary">保存</button>
        </form>
    `;
}

function createUserForm(user = null) {
    return `
        <form id="userForm">
            <div class="form-group">
                <label for="username">ユーザー名</label>
                <input type="text" id="username" name="username" required value="${user?.username || ''}">
            </div>
            <div class="form-group">
                <label for="email">メールアドレス</label>
                <input type="email" id="email" name="email" required value="${user?.email || ''}">
            </div>
            <div class="form-group">
                <label for="role">権限</label>
                <select id="role" name="role" required>
                    <option value="USER">一般ユーザー</option>
                    <option value="ADMIN">管理者</option>
                </select>
            </div>
            <button type="submit" class="btn primary">保存</button>
        </form>
    `;
}

function createBorrowingForm() {
    return `
        <form id="borrowingForm">
            <div class="form-group">
                <label for="bookId">書籍</label>
                <select id="bookId" name="bookId" required>
                    <option value="">選択してください</option>
                </select>
            </div>
            <div class="form-group">
                <label for="userId">利用者</label>
                <select id="userId" name="userId" required>
                    <option value="">選択してください</option>
                </select>
            </div>
            <div class="form-group">
                <label for="returnDate">返却予定日</label>
                <input type="date" id="returnDate" name="returnDate" required>
            </div>
            <button type="submit" class="btn primary">登録</button>
        </form>
    `;
}

// データ操作
async function handleFormSubmit(e) {
    e.preventDefault();
    const formId = e.target.id;
    const formData = new FormData(e.target);
    const data = Object.fromEntries(formData.entries());

    try {
        let response;
        switch (formId) {
            case 'bookForm':
                response = await saveBook(data);
                break;
            case 'userForm':
                response = await saveUser(data);
                break;
            case 'borrowingForm':
                response = await saveBorrowing(data);
                break;
        }

        if (response.ok) {
            closeModal();
            refreshCurrentPage();
        }
    } catch (error) {
        console.error('Error:', error);
        alert('エラーが発生しました');
    }
}

// API呼び出し
async function saveBook(data) {
    return await fetch('/api/books', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(data)
    });
}

async function saveUser(data) {
    return await fetch('/api/users', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(data)
    });
}

async function saveBorrowing(data) {
    return await fetch('/api/borrowings', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(data)
    });
}

// チャート初期化
function initializeCharts() {
    // 人気書籍チャート
    const popularBooksCtx = document.getElementById('popularBooksChart')?.getContext('2d');
    if (popularBooksCtx) {
        new Chart(popularBooksCtx, {
            type: 'bar',
            data: {
                labels: ['書籍A', '書籍B', '書籍C', '書籍D', '書籍E'],
                datasets: [{
                    label: '貸出回数',
                    data: [12, 19, 3, 5, 2],
                    backgroundColor: 'rgba(33, 150, 243, 0.5)'
                }]
            }
        });
    }

    // 月間貸出推移チャート
    const monthlyBorrowingsCtx = document.getElementById('monthlyBorrowingsChart')?.getContext('2d');
    if (monthlyBorrowingsCtx) {
        new Chart(monthlyBorrowingsCtx, {
            type: 'line',
            data: {
                labels: ['1月', '2月', '3月', '4月', '5月'],
                datasets: [{
                    label: '貸出数',
                    data: [65, 59, 80, 81, 56],
                    borderColor: 'rgb(75, 192, 192)'
                }]
            }
        });
    }
}

// ユーティリティ関数
function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

function refreshCurrentPage() {
    const activePage = document.querySelector('.page.active');
    if (activePage) {
        loadPageData(activePage.id);
    }
}

function loadPageData(pageId) {
    switch (pageId) {
        case 'dashboard':
            loadDashboardData();
            break;
        case 'books':
            loadBooks();
            break;
        case 'users':
            loadUsers();
            break;
        case 'borrowing':
            loadBorrowings();
            break;
        case 'statistics':
            loadStatistics();
            break;
    }
}

// データ読み込み関数
async function loadDashboardData() {
    try {
        const response = await fetch('/api/dashboard');
        const data = await response.json();
        updateDashboardUI(data);
    } catch (error) {
        console.error('Error loading dashboard data:', error);
    }
}

function updateDashboardUI(data) {
    // ダッシュボードの更新処理
}

// 検索処理
function handleSearch(section, searchTerm) {
    switch (section) {
        case 'books':
            searchBooks(searchTerm);
            break;
        case 'users':
            searchUsers(searchTerm);
            break;
        case 'borrowing':
            searchBorrowings(searchTerm);
            break;
    }
}

async function searchBooks(term) {
    try {
        const response = await fetch(`/api/books/search?term=${encodeURIComponent(term)}`);
        const books = await response.json();
        updateBooksList(books);
    } catch (error) {
        console.error('Error searching books:', error);
    }
}

function updateBooksList(books) {
    const tbody = document.getElementById('booksList');
    if (!tbody) return;

    tbody.innerHTML = books.map(book => `
        <tr>
            <td>${book.id}</td>
            <td>${book.title}</td>
            <td>${book.author}</td>
            <td>${book.category}</td>
            <td>
                <span class="badge ${getStatusBadgeClass(book.status)}">
                    ${getStatusText(book.status)}
                </span>
            </td>
            <td>
                <button class="btn" onclick="editBook(${book.id})">
                    <span class="material-icons">edit</span>
                </button>
                <button class="btn" onclick="deleteBook(${book.id})">
                    <span class="material-icons">delete</span>
                </button>
            </td>
        </tr>
    `).join('');
}

function getStatusBadgeClass(status) {
    switch (status) {
        case 0: return 'success';
        case 1: return 'warning';
        case 2: return 'danger';
        default: return '';
    }
}

function getStatusText(status) {
    switch (status) {
        case 0: return '利用可能';
        case 1: return '貸出中';
        case 2: return '予約済み';
        default: return '不明';
    }
}
