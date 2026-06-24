// Configuration des URLs des services (surchargeable via config.js)
const API_URLS = window.MOTUS_API_URLS || {
    player: 'http://localhost:8082',
    game: 'http://localhost:8081',
    score: 'http://localhost:8083'
};

// État de l'application
let appState = {
    playerId: null,
    gameId: null,
    currentGame: null,
    currentPlayer: null,
    wordLength: 0,
    firstLetter: null,
    difficulty: 'EASY'
};

// Éléments du DOM
const authSection = document.getElementById('authSection');
const gameSection = document.getElementById('gameSection');
const statsSection = document.getElementById('statsSection');
const loginPanel = document.getElementById('loginPanel');
const registerPanel = document.getElementById('registerPanel');
const authPanelTitle = document.getElementById('authPanelTitle');
const loginUsernameInput = document.getElementById('loginUsernameInput');
const loginPasswordInput = document.getElementById('loginPasswordInput');
const registerUsernameInput = document.getElementById('registerUsernameInput');
const registerEmailInput = document.getElementById('registerEmailInput');
const registerPasswordInput = document.getElementById('registerPasswordInput');
const registerPasswordConfirmInput = document.getElementById('registerPasswordConfirmInput');
const loginBtn = document.getElementById('loginBtn');
const registerBtn = document.getElementById('registerBtn');
const showRegisterBtn = document.getElementById('showRegisterBtn');
const showLoginBtn = document.getElementById('showLoginBtn');
const logoutBtn = document.getElementById('logoutBtn');
const authError = document.getElementById('authError');
const registerError = document.getElementById('registerError');
const registerSuccess = document.getElementById('registerSuccess');
const accountMenuBtn = document.getElementById('accountMenuBtn');
const accountMenuLabel = document.getElementById('accountMenuLabel');
const accountDropdown = document.getElementById('accountDropdown');
const changeEmailMenuBtn = document.getElementById('changeEmailMenuBtn');
const changeEmailModal = document.getElementById('changeEmailModal');
const changeEmailSaveBtn = document.getElementById('changeEmailSaveBtn');
const changeEmailCancelBtn = document.getElementById('changeEmailCancelBtn');
const changeEmailError = document.getElementById('changeEmailError');
const changePasswordMenuBtn = document.getElementById('changePasswordMenuBtn');
const changePasswordModal = document.getElementById('changePasswordModal');
const changePasswordSaveBtn = document.getElementById('changePasswordSaveBtn');
const changePasswordCancelBtn = document.getElementById('changePasswordCancelBtn');
const changePasswordError = document.getElementById('changePasswordError');
const newGameBtn = document.getElementById('newGameBtn');
const guessLetterGrid = document.getElementById('guessLetterGrid');
const submitGuessBtn = document.getElementById('submitGuessBtn');
const attemptsRemaining = document.getElementById('attemptsRemaining');
const gameMessage = document.getElementById('gameMessage');
const attemptsList = document.getElementById('attemptsList');
const wordLengthInfo = document.getElementById('wordLengthInfo');
const newGameDropdown = document.getElementById('newGameDropdown');
const gameDifficultyDisplay = document.getElementById('gameDifficultyDisplay');

// Éléments admin
const adminSection = document.getElementById('adminSection');
const adminLoginModal = document.getElementById('adminLoginModal');
const adminEditModal = document.getElementById('adminEditModal');
const adminAccessBtn = document.getElementById('adminAccessBtn');
const adminPasswordInput = document.getElementById('adminPasswordInput');
const adminLoginBtn = document.getElementById('adminLoginBtn');
const adminLoginCancelBtn = document.getElementById('adminLoginCancelBtn');
const adminLoginError = document.getElementById('adminLoginError');
const adminLogoutBtn = document.getElementById('adminLogoutBtn');
const adminSearchBtn = document.getElementById('adminSearchBtn');
const adminResetSearchBtn = document.getElementById('adminResetSearchBtn');
const adminAdvancedSearchToggle = document.getElementById('adminAdvancedSearchToggle');
const adminAdvancedSearch = document.getElementById('adminAdvancedSearch');
const adminResultsBody = document.getElementById('adminResultsBody');
const adminMessage = document.getElementById('adminMessage');
const adminSaveBtn = document.getElementById('adminSaveBtn');
const adminEditCancelBtn = document.getElementById('adminEditCancelBtn');
const adminEditError = document.getElementById('adminEditError');
const adminTabResults = document.getElementById('adminTabResults');
const adminTabWords = document.getElementById('adminTabWords');
const adminTabUsers = document.getElementById('adminTabUsers');
const adminResultsPanel = document.getElementById('adminResultsPanel');
const adminWordsPanel = document.getElementById('adminWordsPanel');
const adminUsersPanel = document.getElementById('adminUsersPanel');
const adminUsersBody = document.getElementById('adminUsersBody');
const adminUsersMessage = document.getElementById('adminUsersMessage');
const adminUserModal = document.getElementById('adminUserModal');
const usersTotalInfo = document.getElementById('usersTotalInfo');
const usersPageInfo = document.getElementById('usersPageInfo');
const adminWordModal = document.getElementById('adminWordModal');
const adminWordsBody = document.getElementById('adminWordsBody');
const adminWordsMessage = document.getElementById('adminWordsMessage');
const wordsTotalInfo = document.getElementById('wordsTotalInfo');
const wordsPageInfo = document.getElementById('wordsPageInfo');

const ADMIN_PASSWORD_KEY = 'motusAdminPassword';

let wordsAdminState = {
    page: 0,
    size: 20,
    totalPages: 1
};

let usersAdminState = {
    page: 0,
    size: 20,
    totalPages: 1
};

// Initialisations
document.addEventListener('DOMContentLoaded', () => {
    loginBtn.addEventListener('click', handleLogin);
    registerBtn.addEventListener('click', handleRegister);
    showRegisterBtn.addEventListener('click', showRegisterPanel);
    showLoginBtn.addEventListener('click', showLoginPanel);
    logoutBtn.addEventListener('click', handleLogout);
    accountMenuBtn.addEventListener('click', toggleAccountDropdown);
    changeEmailMenuBtn.addEventListener('click', openChangeEmailModal);
    changeEmailSaveBtn.addEventListener('click', handleChangeEmail);
    changeEmailCancelBtn.addEventListener('click', closeChangeEmailModal);
    changePasswordMenuBtn.addEventListener('click', openChangePasswordModal);
    changePasswordSaveBtn.addEventListener('click', handleChangePassword);
    changePasswordCancelBtn.addEventListener('click', closeChangePasswordModal);
    document.addEventListener('click', handleAccountDropdownOutsideClick);
    newGameBtn.addEventListener('click', toggleNewGameDropdown);
    newGameDropdown.querySelectorAll('.new-game-dropdown-item').forEach(item => {
        item.addEventListener('click', () => selectNewGameDifficulty(item.dataset.difficulty));
    });
    document.addEventListener('click', handleNewGameDropdownOutsideClick);
    submitGuessBtn.addEventListener('click', handleSubmitGuess);

    adminAccessBtn.addEventListener('click', openAdminLoginModal);
    adminLoginBtn.addEventListener('click', handleAdminLogin);
    adminLoginCancelBtn.addEventListener('click', closeAdminLoginModal);
    adminLogoutBtn.addEventListener('click', handleAdminLogout);
    adminSearchBtn.addEventListener('click', loadAdminResults);
    adminResetSearchBtn.addEventListener('click', resetAdminSearch);
    adminAdvancedSearchToggle.addEventListener('click', toggleAdminAdvancedSearch);
    adminSaveBtn.addEventListener('click', saveAdminEdit);
    adminEditCancelBtn.addEventListener('click', closeAdminEditModal);
    adminTabResults.addEventListener('click', () => switchAdminTab('results'));
    adminTabWords.addEventListener('click', () => switchAdminTab('words'));
    adminTabUsers.addEventListener('click', () => switchAdminTab('users'));
    document.getElementById('userSearchBtn').addEventListener('click', () => { usersAdminState.page = 0; loadAdminUsers(); });
    document.getElementById('userResetSearchBtn').addEventListener('click', resetUserSearch);
    document.getElementById('userAddBtn').addEventListener('click', () => openUserModal());
    document.getElementById('usersPrevPageBtn').addEventListener('click', () => changeUsersPage(-1));
    document.getElementById('usersNextPageBtn').addEventListener('click', () => changeUsersPage(1));
    document.getElementById('usersPageInput').addEventListener('change', goToUsersPage);
    document.getElementById('adminUserSaveBtn').addEventListener('click', saveUser);
    document.getElementById('adminUserCancelBtn').addEventListener('click', closeUserModal);
    document.getElementById('wordSearchBtn').addEventListener('click', () => { wordsAdminState.page = 0; loadAdminWords(); });
    document.getElementById('wordResetSearchBtn').addEventListener('click', resetWordSearch);
    document.getElementById('wordAddBtn').addEventListener('click', () => openWordModal());
    document.getElementById('wordsPrevPageBtn').addEventListener('click', () => changeWordsPage(-1));
    document.getElementById('wordsNextPageBtn').addEventListener('click', () => changeWordsPage(1));
    document.getElementById('wordsPageInput').addEventListener('change', goToWordsPage);
    document.getElementById('adminWordSaveBtn').addEventListener('click', saveWord);
    document.getElementById('adminWordCancelBtn').addEventListener('click', closeWordModal);

    adminPasswordInput.addEventListener('keydown', (e) => {
        if (e.key === 'Enter') {
            e.preventDefault();
            handleAdminLogin();
        }
    });

    loginPasswordInput.addEventListener('keydown', (e) => {
        if (e.key === 'Enter') {
            e.preventDefault();
            handleLogin();
        }
    });

    registerPasswordConfirmInput.addEventListener('keydown', (e) => {
        if (e.key === 'Enter') {
            e.preventDefault();
            handleRegister();
        }
    });

    guessLetterGrid.addEventListener('keydown', handleLetterGridKeydown);
    guessLetterGrid.addEventListener('input', handleLetterGridInput);
    guessLetterGrid.addEventListener('paste', handleLetterGridPaste);

    if (sessionStorage.getItem(ADMIN_PASSWORD_KEY)) {
        showAdminDashboard();
    }
});

// ============ Saisie lettre par lettre ============
function buildLetterInputs(wordLength, firstLetter) {
    appState.wordLength = wordLength;
    appState.firstLetter = firstLetter ? firstLetter.toUpperCase() : null;
    guessLetterGrid.innerHTML = '';
    guessLetterGrid.style.setProperty('--letter-count', wordLength);

    for (let i = 0; i < wordLength; i++) {
        const tile = document.createElement('div');
        tile.className = 'letter-tile input-tile';

        const input = document.createElement('input');
        input.type = 'text';
        input.maxLength = 1;
        input.className = 'letter-input';
        input.setAttribute('aria-label', `Lettre ${i + 1}`);
        input.placeholder = ' ';
        input.dataset.index = String(i);
        input.autocomplete = 'off';
        input.spellcheck = false;

        if (i === 0 && appState.firstLetter) {
            input.value = appState.firstLetter;
            input.readOnly = true;
            input.classList.add('letter-locked');
            input.setAttribute('aria-label', `Lettre ${i + 1} (indice)`);
            tile.classList.add('locked-tile');
        }

        const bar = document.createElement('span');
        bar.className = 'letter-bar';

        tile.appendChild(input);
        tile.appendChild(bar);
        guessLetterGrid.appendChild(tile);
    }
}

function isLockedInput(input) {
    return input?.classList.contains('letter-locked');
}

function getFirstEditableIndex() {
    return appState.firstLetter ? 1 : 0;
}

function applyLockedFirstLetter() {
    const inputs = getLetterInputs();
    if (inputs[0] && appState.firstLetter) {
        inputs[0].value = appState.firstLetter;
    }
}

function getLetterInputs() {
    return Array.from(guessLetterGrid.querySelectorAll('.letter-input'));
}

function getGuessFromInputs() {
    return getLetterInputs().map(input => input.value.trim().toUpperCase()).join('');
}

function clearLetterInputs() {
    getLetterInputs().forEach((input, index) => {
        if (index === 0 && appState.firstLetter) {
            input.value = appState.firstLetter;
        } else {
            input.value = '';
        }
    });
    const focusIndex = getFirstEditableIndex();
    const inputToFocus = getLetterInputs()[focusIndex];
    if (inputToFocus) inputToFocus.focus();
}

function setLetterInputsDisabled(disabled) {
    getLetterInputs().forEach(input => {
        input.disabled = disabled;
    });
}

function handleLetterGridInput(e) {
    const input = e.target;
    if (!input.classList.contains('letter-input')) return;

    if (isLockedInput(input)) {
        input.value = appState.firstLetter;
        return;
    }

    input.value = input.value.replace(/[^a-zA-ZÀ-ÿ]/g, '').toUpperCase();

    const index = parseInt(input.dataset.index, 10);
    if (input.value && index < appState.wordLength - 1) {
        getLetterInputs()[index + 1].focus();
    }
}

function handleLetterGridKeydown(e) {
    const input = e.target;
    if (!input.classList.contains('letter-input')) return;

    const index = parseInt(input.dataset.index, 10);
    const inputs = getLetterInputs();
    const firstEditable = getFirstEditableIndex();

    if (isLockedInput(input) && (e.key === 'Backspace' || e.key === 'Delete')) {
        e.preventDefault();
        return;
    }

    if (e.key === 'Backspace') {
        if (isLockedInput(input)) {
            e.preventDefault();
            return;
        }
        if (!input.value && index > firstEditable) {
            e.preventDefault();
            const prev = inputs[index - 1];
            if (!isLockedInput(prev)) {
                prev.focus();
                prev.value = '';
            } else {
                input.focus();
            }
        }
    } else if (e.key === 'ArrowLeft' && index > firstEditable) {
        e.preventDefault();
        inputs[index - 1].focus();
    } else if (e.key === 'ArrowRight' && index < inputs.length - 1) {
        e.preventDefault();
        inputs[index + 1].focus();
    } else if (e.key === 'Enter') {
        e.preventDefault();
        handleSubmitGuess();
    }
}

function handleLetterGridPaste(e) {
    e.preventDefault();
    const startIndex = getFirstEditableIndex();
    let pasted = (e.clipboardData.getData('text') || '')
        .replace(/[^a-zA-ZÀ-ÿ]/g, '')
        .toUpperCase();

    if (appState.firstLetter && pasted.startsWith(appState.firstLetter)) {
        pasted = pasted.slice(1);
    }

    pasted = pasted.slice(0, appState.wordLength - startIndex);

    const inputs = getLetterInputs();
    pasted.split('').forEach((char, i) => {
        const target = inputs[startIndex + i];
        if (target && !isLockedInput(target)) {
            target.value = char;
        }
    });

    applyLockedFirstLetter();
    const focusIndex = Math.min(startIndex + pasted.length, appState.wordLength - 1);
    if (inputs[focusIndex] && !isLockedInput(inputs[focusIndex])) {
        inputs[focusIndex].focus();
    } else if (inputs[startIndex]) {
        inputs[startIndex].focus();
    }
}

function createLetterTile(letter, status) {
    const tile = document.createElement('div');
    tile.className = `letter-tile result-tile ${status ? status.toLowerCase() : 'absent'}`;

    const value = document.createElement('span');
    value.className = 'letter-value';
    value.textContent = letter;

    const bar = document.createElement('span');
    bar.className = 'letter-bar';

    tile.appendChild(value);
    tile.appendChild(bar);
    return tile;
}

function createLetterGrid(children, wordLength) {
    const grid = document.createElement('div');
    grid.className = 'letter-grid';
    grid.style.setProperty('--letter-count', wordLength);
    children.forEach(child => grid.appendChild(child));
    return grid;
}

// ============ Authentification ============

const PASSWORD_RULE_MESSAGE = 'Le mot de passe doit contenir au moins 8 caractères, une majuscule, une minuscule et un chiffre';

function validatePassword(password) {
    if (!password || password.length < 8) {
        return PASSWORD_RULE_MESSAGE;
    }
    if (!/[A-Z]/.test(password)) {
        return PASSWORD_RULE_MESSAGE;
    }
    if (!/[a-z]/.test(password)) {
        return PASSWORD_RULE_MESSAGE;
    }
    if (!/[0-9]/.test(password)) {
        return PASSWORD_RULE_MESSAGE;
    }
    return null;
}

const EMAIL_RULE_MESSAGE = 'Adresse e-mail invalide';

function validateEmail(email) {
    if (!email || !email.trim()) {
        return 'Veuillez entrer une adresse e-mail';
    }
    if (!/^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/.test(email.trim())) {
        return EMAIL_RULE_MESSAGE;
    }
    return null;
}

async function parseApiError(response, fallbackMessage) {
    try {
        const err = await response.json();
        return err.message || err.error || fallbackMessage;
    } catch {
        return fallbackMessage;
    }
}

function showLoginPanel() {
    loginPanel.classList.remove('hidden');
    registerPanel.classList.add('hidden');
    authPanelTitle.textContent = 'Connexion';
    authError.textContent = '';
    registerError.textContent = '';
    registerSuccess.classList.add('hidden');
}

function showRegisterPanel() {
    loginPanel.classList.add('hidden');
    registerPanel.classList.remove('hidden');
    authPanelTitle.textContent = 'Créer un compte';
    authError.textContent = '';
    registerError.textContent = '';
    registerSuccess.classList.add('hidden');
}

async function handleRegister() {
    const username = registerUsernameInput.value.trim();
    const email = registerEmailInput.value.trim();
    const password = registerPasswordInput.value;
    const confirmPassword = registerPasswordConfirmInput.value;

    registerError.textContent = '';
    registerSuccess.classList.add('hidden');

    if (!username) {
        registerError.textContent = 'Veuillez entrer un pseudo';
        return;
    }

    const emailError = validateEmail(email);
    if (emailError) {
        registerError.textContent = emailError;
        return;
    }

    const passwordError = validatePassword(password);
    if (passwordError) {
        registerError.textContent = passwordError;
        return;
    }

    if (password !== confirmPassword) {
        registerError.textContent = 'Les mots de passe ne correspondent pas';
        return;
    }

    try {
        const response = await fetch(`${API_URLS.player}/players/register`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, email, password })
        });

        if (!response.ok) {
            throw new Error(await parseApiError(response, 'Erreur lors de la création du compte'));
        }

        showLoginPanel();
        loginUsernameInput.value = username;
        loginPasswordInput.value = '';
        authError.textContent = '';
        registerUsernameInput.value = '';
        registerEmailInput.value = '';
        registerPasswordInput.value = '';
        registerPasswordConfirmInput.value = '';
        authError.className = 'game-message success';
        authError.textContent = 'Compte créé avec succès. Connectez-vous avec votre pseudo et mot de passe.';
    } catch (error) {
        registerError.textContent = error.message;
        console.error('Register error:', error);
    }
}

async function handleLogin() {
    const username = loginUsernameInput.value.trim();
    const password = loginPasswordInput.value;

    authError.className = 'error-message';

    if (!username) {
        authError.textContent = 'Veuillez entrer un pseudo';
        return;
    }
    if (!password) {
        authError.textContent = 'Veuillez entrer votre mot de passe';
        return;
    }

    try {
        authError.textContent = '';

        const response = await fetch(`${API_URLS.player}/players/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password })
        });

        if (!response.ok) {
            throw new Error(await parseApiError(response, 'Erreur lors de la connexion'));
        }

        let data = await response.json();
        if (data && data.content) data = data.content;
        const playerId = data.id || data.playerId;
        const playerUsername = data.username || username;
        const playerEmail = data.email || '';

        appState.playerId = playerId;
        appState.currentPlayer = { id: playerId, username: playerUsername, email: playerEmail };

        accountMenuLabel.textContent = playerUsername;

        authSection.classList.add('hidden');
        adminSection.classList.add('hidden');
        gameSection.classList.remove('hidden');
        statsSection.classList.remove('hidden');

        await loadPlayerStats();
        await loadLeaderboard();
        await startNewGameWithDifficulty('EASY');
    } catch (error) {
        authError.textContent = error.message;
        console.error('Login error:', error);
    }
}

function toggleAccountDropdown(event) {
    event.stopPropagation();
    const isOpen = !accountDropdown.classList.contains('hidden');
    closeNewGameDropdown();
    if (isOpen) {
        closeAccountDropdown();
    } else {
        openAccountDropdown();
    }
}

function openAccountDropdown() {
    accountDropdown.classList.remove('hidden');
    accountMenuBtn.setAttribute('aria-expanded', 'true');
}

function closeAccountDropdown() {
    accountDropdown.classList.add('hidden');
    accountMenuBtn.setAttribute('aria-expanded', 'false');
}

function handleAccountDropdownOutsideClick(event) {
    if (accountDropdown.classList.contains('hidden')) return;
    if (event.target.closest('.account-dropdown-wrapper')) return;
    closeAccountDropdown();
}

function openChangeEmailModal() {
    closeAccountDropdown();
    document.getElementById('newEmailInput').value = appState.currentPlayer?.email || '';
    changeEmailError.textContent = '';
    changeEmailModal.classList.remove('hidden');
}

function closeChangeEmailModal() {
    changeEmailModal.classList.add('hidden');
    changeEmailError.textContent = '';
}

async function handleChangeEmail() {
    const email = document.getElementById('newEmailInput').value.trim();

    changeEmailError.textContent = '';

    const emailError = validateEmail(email);
    if (emailError) {
        changeEmailError.textContent = emailError;
        return;
    }

    try {
        const response = await fetch(`${API_URLS.player}/players/${appState.playerId}/email`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email })
        });

        if (!response.ok) {
            throw new Error(await parseApiError(response, 'Erreur lors du changement d\'e-mail'));
        }

        let data = await response.json();
        if (data && data.content) data = data.content;
        if (appState.currentPlayer) {
            appState.currentPlayer.email = data.email || email;
        }

        closeChangeEmailModal();
        gameMessage.className = 'game-message success';
        gameMessage.textContent = 'Adresse e-mail modifiée avec succès';
    } catch (error) {
        changeEmailError.textContent = error.message;
    }
}

function openChangePasswordModal() {
    closeAccountDropdown();
    document.getElementById('oldPasswordInput').value = '';
    document.getElementById('newPasswordInput').value = '';
    document.getElementById('confirmNewPasswordInput').value = '';
    changePasswordError.textContent = '';
    changePasswordModal.classList.remove('hidden');
}

function closeChangePasswordModal() {
    changePasswordModal.classList.add('hidden');
    changePasswordError.textContent = '';
}

async function handleChangePassword() {
    const oldPassword = document.getElementById('oldPasswordInput').value;
    const newPassword = document.getElementById('newPasswordInput').value;
    const confirmPassword = document.getElementById('confirmNewPasswordInput').value;

    changePasswordError.textContent = '';

    if (!oldPassword || !newPassword || !confirmPassword) {
        changePasswordError.textContent = 'Tous les champs sont obligatoires';
        return;
    }

    const passwordError = validatePassword(newPassword);
    if (passwordError) {
        changePasswordError.textContent = passwordError;
        return;
    }

    if (newPassword !== confirmPassword) {
        changePasswordError.textContent = 'Les nouveaux mots de passe ne correspondent pas';
        return;
    }

    try {
        const response = await fetch(`${API_URLS.player}/players/${appState.playerId}/password`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ oldPassword, newPassword })
        });

        if (!response.ok) {
            throw new Error(await parseApiError(response, 'Erreur lors du changement de mot de passe'));
        }

        closeChangePasswordModal();
        gameMessage.className = 'game-message success';
        gameMessage.textContent = 'Mot de passe modifié avec succès';
    } catch (error) {
        changePasswordError.textContent = error.message;
    }
}

function handleLogout() {
    appState.playerId = null;
    appState.gameId = null;
    appState.currentGame = null;
    appState.currentPlayer = null;
    appState.wordLength = 0;
    appState.firstLetter = null;
    appState.difficulty = 'EASY';
    updateGameDifficultyDisplay('-');
    closeNewGameDropdown();
    closeAccountDropdown();
    closeChangeEmailModal();
    closeChangePasswordModal();

    loginUsernameInput.value = '';
    loginPasswordInput.value = '';
    registerUsernameInput.value = '';
    registerEmailInput.value = '';
    registerPasswordInput.value = '';
    registerPasswordConfirmInput.value = '';
    authError.textContent = '';
    authError.className = 'error-message';
    registerError.textContent = '';
    registerSuccess.classList.add('hidden');
    accountMenuLabel.textContent = 'Mon compte';
    showLoginPanel();
    authSection.classList.remove('hidden');
    adminSection.classList.add('hidden');
    gameSection.classList.add('hidden');
    statsSection.classList.add('hidden');
    guessLetterGrid.innerHTML = '';
    setLetterInputsDisabled(false);
    submitGuessBtn.disabled = false;
}

// ============ Jeu ============

function formatWordDifficulty(difficulty) {
    const labels = { EASY: 'Facile', MEDIUM: 'Moyen', HARD: 'Difficile' };
    return labels[difficulty] ?? difficulty ?? '-';
}

function updateGameDifficultyDisplay(difficulty) {
    if (gameDifficultyDisplay) {
        gameDifficultyDisplay.textContent = formatWordDifficulty(difficulty);
    }
}

function toggleNewGameDropdown(event) {
    event.stopPropagation();
    if (!appState.playerId) {
        gameMessage.className = 'game-message error';
        gameMessage.textContent = 'Erreur: Vous devez être connecté';
        return;
    }
    const isOpen = !newGameDropdown.classList.contains('hidden');
    closeAccountDropdown();
    if (isOpen) {
        closeNewGameDropdown();
    } else {
        openNewGameDropdown();
    }
}

function openNewGameDropdown() {
    newGameDropdown.classList.remove('hidden');
    newGameBtn.setAttribute('aria-expanded', 'true');
}

function closeNewGameDropdown() {
    newGameDropdown.classList.add('hidden');
    newGameBtn.setAttribute('aria-expanded', 'false');
}

function handleNewGameDropdownOutsideClick(event) {
    if (newGameDropdown.classList.contains('hidden')) return;
    if (event.target.closest('.new-game-dropdown-wrapper')) return;
    closeNewGameDropdown();
}

async function selectNewGameDifficulty(difficulty) {
    closeNewGameDropdown();
    await startNewGameWithDifficulty(difficulty || 'EASY');
}

async function startNewGameWithDifficulty(difficulty) {
    if (!appState.playerId) {
        gameMessage.className = 'game-message error';
        gameMessage.textContent = 'Erreur: Vous devez être connecté';
        return;
    }

    try {
        gameMessage.className = 'game-message';
        gameMessage.textContent = '';
        const response = await fetch(`${API_URLS.game}/games`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ playerId: appState.playerId, difficulty })
        });

        if (!response.ok) {
            throw new Error('Erreur lors de la création de la partie');
        }

        let data = await response.json();
        if (data && data.content) data = data.content;
        appState.gameId = data.gameId || data.id;
        appState.currentGame = data;

        const wordLength = data.wordLength ?? 0;
        const firstLetter = data.firstLetter ?? null;
        appState.difficulty = data.difficulty || difficulty;
        attemptsRemaining.textContent = data.attemptsRemaining;
        updateGameDifficultyDisplay(appState.difficulty);
        wordLengthInfo.textContent = firstLetter
            ? `Le mot mystère contient ${wordLength} lettres.`
            : `Le mot mystère contient ${wordLength} lettre${wordLength > 1 ? 's' : ''}.`;

        buildLetterInputs(wordLength, firstLetter);
        setLetterInputsDisabled(false);
        submitGuessBtn.disabled = false;
        attemptsList.innerHTML = '';

        gameMessage.className = 'game-message';
        gameMessage.textContent = firstLetter
            ? `🎮 C'est parti ! Le mot de ${wordLength} lettres commence par « ${firstLetter.toUpperCase()} ».`
            : `🎮 C'est parti ! Devinez le mot de ${wordLength} lettres en 6 essais maximum.`;

        const focusInput = getLetterInputs()[getFirstEditableIndex()];
        if (focusInput) focusInput.focus();
    } catch (error) {
        gameMessage.className = 'game-message error';
        gameMessage.textContent = 'Erreur: ' + error.message;
        console.error('New game error:', error);
    }
}

async function handleSubmitGuess() {
    const guessWord = getGuessFromInputs();

    if (!guessWord || guessWord.length < appState.wordLength) {
        gameMessage.className = 'game-message error';
        gameMessage.textContent = `Veuillez remplir les ${appState.wordLength} lettres`;
        return;
    }

    if (!appState.gameId) {
        gameMessage.className = 'game-message error';
        gameMessage.textContent = 'Aucune partie active';
        return;
    }

    try {
        gameMessage.className = 'game-message';
        gameMessage.textContent = '⏳ Vérification du mot...';
        submitGuessBtn.disabled = true;

        const response = await fetch(`${API_URLS.game}/games/${appState.gameId}/guesses`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ guessWord: guessWord })
        });

        if (!response.ok) {
            const contentType = response.headers.get('content-type') || '';
            let errorMsg = `Erreur ${response.status}`;
            if (contentType.includes('application/json')) {
                const errorData = await response.json().catch(() => ({}));
                errorMsg = errorData.message || errorData.error || errorMsg;
            } else {
                const text = await response.text().catch(() => '');
                if (text) errorMsg = text;
            }
            throw new Error(errorMsg);
        }

        let data = await response.json();
        if (data && data.content) data = data.content;
        appState.currentGame = data;

        attemptsRemaining.textContent = data.attemptsRemaining;
        if (data.firstLetter) {
            appState.firstLetter = data.firstLetter.toUpperCase();
        }
        clearLetterInputs();
        displayAttempts(data.guesses, data.wordLength ?? appState.wordLength);

        if (data.difficulty) {
            appState.difficulty = data.difficulty;
            updateGameDifficultyDisplay(data.difficulty);
        }

        if (data.status === 'WON') {
            const points = data.pointsEarned ?? 0;
            gameMessage.className = 'game-message success';
            gameMessage.textContent = `🎉 Bravo! Mot trouvé en ${data.guesses.length} essai(s) — +${points} point${points > 1 ? 's' : ''} (${formatWordDifficulty(data.difficulty)})`;
            setLetterInputsDisabled(true);
            submitGuessBtn.disabled = true;
            await loadPlayerStats();
            await loadLeaderboard();
        } else if (data.status === 'LOST') {
            gameMessage.className = 'game-message error';
            gameMessage.textContent = `😞 Dommage! Vous avez perdu. Le mot était: ${data.targetWord || 'secret'}`;
            setLetterInputsDisabled(true);
            submitGuessBtn.disabled = true;
            await loadPlayerStats();
            await loadLeaderboard();
        } else {
            gameMessage.className = 'game-message';
            gameMessage.textContent = `✓ Mot accepté! Essais restants: ${data.attemptsRemaining}`;
        }
    } catch (error) {
        gameMessage.className = 'game-message error';
        gameMessage.textContent = 'Erreur: ' + error.message;
        console.error('Guess error:', error);
    } finally {
        if (appState.currentGame?.status !== 'WON' && appState.currentGame?.status !== 'LOST') {
            submitGuessBtn.disabled = false;
        }
    }
}

function displayAttempts(guesses, wordLength) {
    attemptsList.innerHTML = '';

    guesses.forEach((guess) => {
        const row = document.createElement('div');
        row.className = 'attempt-row';

        const tiles = guess.feedback.map(letterFeedback =>
            createLetterTile(letterFeedback.letter, letterFeedback.status)
        );

        row.appendChild(createLetterGrid(tiles, wordLength));
        attemptsList.appendChild(row);
    });
}

// ============ Statistiques ============
async function loadPlayerStats() {
    try {
        const response = await fetch(`${API_URLS.score}/scores/players/${appState.playerId}/stats`);

        if (!response.ok) {
            throw new Error('Erreur lors du chargement des stats');
        }

        const stats = await response.json();

        document.getElementById('totalScore').textContent = stats.totalScore ?? 0;
        document.getElementById('totalGames').textContent = stats.totalGames || 0;
        document.getElementById('totalWins').textContent = stats.totalWins || 0;
        document.getElementById('winRate').textContent = `${(stats.winRate || 0).toFixed(1)}%`;
        document.getElementById('avgAttempts').textContent = `${(stats.averageAttempts || 0).toFixed(2)}`;
    } catch (error) {
        console.error('Load stats error:', error);
    }
}

async function loadLeaderboard() {
    try {
        const response = await fetch(`${API_URLS.score}/scores/leaderboard`);

        if (!response.ok) {
            throw new Error('Erreur lors du chargement du classement');
        }

        const leaderboard = await response.json();
        const leaderboardBody = document.getElementById('leaderboardBody');
        leaderboardBody.innerHTML = '';

        leaderboard.forEach((entry, index) => {
            const username = entry.username ?? 'Joueur inconnu';
            const score = entry.totalScore ?? 0;
            const wins = entry.totalWins ?? entry.wins ?? 0;
            const winRate = entry.winRate ?? 0;
            const avgAttempts = entry.averageAttempts ?? 0;

            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${index + 1}</td>
                <td>${username}</td>
                <td><strong>${score}</strong></td>
                <td>${wins}</td>
                <td>${Number(winRate).toFixed(1)}%</td>
                <td>${Number(avgAttempts).toFixed(2)}</td>
            `;
            leaderboardBody.appendChild(row);
        });
    } catch (error) {
        console.error('Load leaderboard error:', error);
    }
}

// ============ Administration ============
function openAdminLoginModal() {
    adminLoginError.textContent = '';
    adminPasswordInput.value = '';
    adminLoginModal.classList.remove('hidden');
    adminPasswordInput.focus();
}

function closeAdminLoginModal() {
    adminLoginModal.classList.add('hidden');
}

function openAdminEditModal() {
    adminEditModal.classList.remove('hidden');
}

function closeAdminEditModal() {
    adminEditModal.classList.add('hidden');
    adminEditError.textContent = '';
}

function hideAllMainSections() {
    authSection.classList.add('hidden');
    gameSection.classList.add('hidden');
    statsSection.classList.add('hidden');
    adminSection.classList.add('hidden');
}

function showAdminDashboard() {
    hideAllMainSections();
    adminSection.classList.remove('hidden');
    closeAdminLoginModal();
    loadAdminResults();
}

function showAuthScreen() {
    hideAllMainSections();
    authSection.classList.remove('hidden');
}

function showAdminMessage(text, type = '') {
    adminMessage.textContent = text;
    adminMessage.className = 'game-message';
    if (type) adminMessage.classList.add(type);
    adminMessage.classList.remove('hidden');
}

async function handleAdminLogin() {
    const password = adminPasswordInput.value;
    if (!password) {
        adminLoginError.textContent = 'Veuillez entrer le mot de passe';
        return;
    }

    try {
        adminLoginError.textContent = '';
        const response = await fetch(`${API_URLS.score}/scores/admin/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ password })
        });

        const data = await response.json();
        if (!response.ok || !data.authenticated) {
            adminLoginError.textContent = data.message || 'Mot de passe incorrect';
            return;
        }

        sessionStorage.setItem(ADMIN_PASSWORD_KEY, password);
        showAdminDashboard();
    } catch (error) {
        adminLoginError.textContent = 'Erreur: ' + error.message;
    }
}

function handleAdminLogout() {
    sessionStorage.removeItem(ADMIN_PASSWORD_KEY);
    closeAdminEditModal();
    closeWordModal();
    closeUserModal();
    showAuthScreen();
}

function switchAdminTab(tab) {
    adminTabResults.classList.toggle('active', tab === 'results');
    adminTabWords.classList.toggle('active', tab === 'words');
    adminTabUsers.classList.toggle('active', tab === 'users');
    adminResultsPanel.classList.toggle('hidden', tab !== 'results');
    adminWordsPanel.classList.toggle('hidden', tab !== 'words');
    adminUsersPanel.classList.toggle('hidden', tab !== 'users');
    adminMessage.classList.toggle('hidden', tab !== 'results');
    adminWordsMessage.classList.toggle('hidden', tab !== 'words');
    adminUsersMessage.classList.toggle('hidden', tab !== 'users');

    if (tab === 'words') loadAdminWords();
    if (tab === 'users') loadAdminUsers();
}

function showWordsMessage(text, type = '') {
    adminWordsMessage.textContent = text;
    adminWordsMessage.className = 'game-message';
    if (type) adminWordsMessage.classList.add(type);
    adminWordsMessage.classList.remove('hidden');
}

function showUsersMessage(text, type = '') {
    adminUsersMessage.textContent = text;
    adminUsersMessage.className = 'game-message';
    if (type) adminUsersMessage.classList.add(type);
    adminUsersMessage.classList.remove('hidden');
}

async function adminFetch(url, options = {}) {
    const password = sessionStorage.getItem(ADMIN_PASSWORD_KEY);
    const headers = {
        'X-Admin-Password': password || '',
        ...(options.headers || {})
    };
    if (options.body) {
        headers['Content-Type'] = 'application/json';
    }
    return fetch(url, { ...options, headers });
}

function toIsoDateTime(localValue) {
    if (!localValue) return null;
    return localValue.length === 16 ? `${localValue}:00` : localValue;
}

function toSearchStartDate(dateValue) {
    if (!dateValue) return null;
    return `${dateValue}T00:00:00`;
}

function toSearchEndDate(dateValue) {
    if (!dateValue) return null;
    return `${dateValue}T23:59:59`;
}

function toDatetimeLocalValue(isoValue) {
    if (!isoValue) return '';
    return isoValue.substring(0, 16);
}

function formatPlayedAt(isoValue) {
    if (!isoValue) return '-';
    return new Date(isoValue).toLocaleString('fr-FR');
}

function buildAdminSearchParams() {
    const params = new URLSearchParams();
    const username = document.getElementById('searchUsername').value.trim();
    const isWinner = document.getElementById('searchIsWinner').value;
    const startDate = document.getElementById('searchStartDate').value;
    const endDate = document.getElementById('searchEndDate').value;
    const minWinRate = document.getElementById('searchMinWinRate').value;
    const maxWinRate = document.getElementById('searchMaxWinRate').value;
    const minAvgAttempts = document.getElementById('searchMinAvgAttempts').value;
    const maxAvgAttempts = document.getElementById('searchMaxAvgAttempts').value;
    const leaderboardPosition = document.getElementById('searchLeaderboardPosition').value;
    const minPosition = document.getElementById('searchMinPosition').value;
    const maxPosition = document.getElementById('searchMaxPosition').value;

    if (username) params.set('username', username);
    if (isWinner !== '') params.set('isWinner', isWinner);
    const startIso = toSearchStartDate(startDate);
    const endIso = toSearchEndDate(endDate);
    if (startIso) params.set('startDate', startIso);
    if (endIso) params.set('endDate', endIso);
    if (minWinRate) params.set('minWinRate', minWinRate);
    if (maxWinRate) params.set('maxWinRate', maxWinRate);
    if (minAvgAttempts) params.set('minAvgAttempts', minAvgAttempts);
    if (maxAvgAttempts) params.set('maxAvgAttempts', maxAvgAttempts);
    if (leaderboardPosition) params.set('leaderboardPosition', leaderboardPosition);
    if (minPosition) params.set('minLeaderboardPosition', minPosition);
    if (maxPosition) params.set('maxLeaderboardPosition', maxPosition);

    return params;
}

function toggleAdminAdvancedSearch() {
    const isExpanded = !adminAdvancedSearch.classList.contains('hidden');
    if (isExpanded) {
        closeAdminAdvancedSearch();
    } else {
        openAdminAdvancedSearch();
    }
}

function openAdminAdvancedSearch() {
    adminAdvancedSearch.classList.remove('hidden');
    adminAdvancedSearchToggle.classList.add('expanded');
    adminAdvancedSearchToggle.setAttribute('aria-expanded', 'true');
}

function closeAdminAdvancedSearch() {
    adminAdvancedSearch.classList.add('hidden');
    adminAdvancedSearchToggle.classList.remove('expanded');
    adminAdvancedSearchToggle.setAttribute('aria-expanded', 'false');
}

function resetAdminSearch() {
    document.getElementById('searchUsername').value = '';
    document.getElementById('searchIsWinner').value = '';
    document.getElementById('searchStartDate').value = '';
    document.getElementById('searchEndDate').value = '';
    document.getElementById('searchMinWinRate').value = '';
    document.getElementById('searchMaxWinRate').value = '';
    document.getElementById('searchMinAvgAttempts').value = '';
    document.getElementById('searchMaxAvgAttempts').value = '';
    document.getElementById('searchLeaderboardPosition').value = '';
    document.getElementById('searchMinPosition').value = '';
    document.getElementById('searchMaxPosition').value = '';
    loadAdminResults();
}

async function loadAdminResults() {
    try {
        const params = buildAdminSearchParams();
        const response = await adminFetch(`${API_URLS.score}/scores/admin/results?${params.toString()}`);

        if (response.status === 401) {
            handleAdminLogout();
            adminLoginError.textContent = 'Session expirée, reconnectez-vous';
            openAdminLoginModal();
            return;
        }

        if (!response.ok) {
            throw new Error('Erreur lors du chargement des résultats');
        }

        const results = await response.json();
        renderAdminResults(results);
        showAdminMessage(`${results.length} résultat(s) trouvé(s)`);
    } catch (error) {
        showAdminMessage('Erreur: ' + error.message, 'error');
    }
}

function renderAdminResults(results) {
    adminResultsBody.innerHTML = '';

    if (results.length === 0) {
        adminResultsBody.innerHTML = '<tr><td colspan="11" class="empty-row">Aucun résultat</td></tr>';
        return;
    }

    results.forEach(result => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${result.id}</td>
            <td>${result.gameId ?? '-'}</td>
            <td>${result.username ?? 'Inconnu'}</td>
            <td>${result.playerLeaderboardPosition ?? '-'}</td>
            <td>${Number(result.playerWinRate ?? 0).toFixed(1)}%</td>
            <td>${Number(result.playerAvgAttempts ?? 0).toFixed(2)}</td>
            <td>${result.targetWord ?? '-'}</td>
            <td>${result.attemptsCount ?? '-'}</td>
            <td>${result.isWinner ? '✅ Victoire' : '❌ Défaite'}</td>
            <td>${formatPlayedAt(result.playedAt)}</td>
            <td class="admin-actions">
                <button class="btn btn-secondary btn-small" data-action="edit" data-id="${result.id}">Modifier</button>
                <button class="btn btn-secondary btn-small btn-danger" data-action="delete" data-id="${result.id}">Supprimer</button>
            </td>
        `;
        adminResultsBody.appendChild(row);
    });

    adminResultsBody.querySelectorAll('button[data-action="edit"]').forEach(btn => {
        btn.addEventListener('click', () => openEditResult(btn.dataset.id));
    });
    adminResultsBody.querySelectorAll('button[data-action="delete"]').forEach(btn => {
        btn.addEventListener('click', () => deleteAdminResult(btn.dataset.id));
    });
}

async function openEditResult(id) {
    try {
        const response = await adminFetch(`${API_URLS.score}/scores/admin/results/${id}`);
        if (!response.ok) {
            throw new Error('Impossible de charger le résultat');
        }
        const result = await response.json();

    document.getElementById('editResultId').value = result.id;
    document.getElementById('editGameId').value = result.gameId ?? '';
    document.getElementById('editUsername').value = result.username ?? '';
    document.getElementById('editTargetWord').value = result.targetWord ?? '';
    document.getElementById('editAttemptsCount').value = result.attemptsCount ?? '';
    document.getElementById('editIsWinner').value = String(result.isWinner ?? false);
    document.getElementById('editPlayedAt').value = toDatetimeLocalValue(result.playedAt);
    adminEditError.textContent = '';
    openAdminEditModal();
    } catch (error) {
        showAdminMessage('Erreur: ' + error.message, 'error');
    }
}

async function saveAdminEdit() {
    const id = document.getElementById('editResultId').value;
    const username = document.getElementById('editUsername').value.trim();
    if (!username) {
        adminEditError.textContent = 'Le pseudo du joueur est obligatoire';
        return;
    }

    const payload = {
        username,
        targetWord: document.getElementById('editTargetWord').value.trim().toUpperCase(),
        attemptsCount: Number(document.getElementById('editAttemptsCount').value),
        isWinner: document.getElementById('editIsWinner').value === 'true',
        playedAt: toIsoDateTime(document.getElementById('editPlayedAt').value)
    };

    try {
        const response = await adminFetch(`${API_URLS.score}/scores/admin/results/${id}`, {
            method: 'PUT',
            body: JSON.stringify(payload)
        });

        if (!response.ok) {
            const err = await response.json().catch(() => ({}));
            throw new Error(err.message || 'Erreur lors de la modification');
        }

        closeAdminEditModal();
        showAdminMessage('Résultat modifié avec succès', 'success');
        await loadAdminResults();
    } catch (error) {
        adminEditError.textContent = error.message;
    }
}

async function deleteAdminResult(id) {
    if (!confirm('Supprimer ce résultat ?')) return;

    try {
        const response = await adminFetch(`${API_URLS.score}/scores/admin/results/${id}`, {
            method: 'DELETE'
        });

        if (!response.ok) {
            const err = await response.json().catch(() => ({}));
            throw new Error(err.message || 'Erreur lors de la suppression');
        }

        showAdminMessage('Résultat supprimé', 'success');
        await loadAdminResults();
    } catch (error) {
        showAdminMessage('Erreur: ' + error.message, 'error');
    }
}

// ============ Admin dictionnaire ============

function buildWordSearchParams() {
    const params = new URLSearchParams();
    const search = document.getElementById('wordSearchInput').value.trim();
    const difficulty = document.getElementById('wordDifficultyFilter').value;
    const minLength = document.getElementById('wordMinLength').value;
    const maxLength = document.getElementById('wordMaxLength').value;

    if (search) params.set('search', search.toUpperCase());
    if (difficulty) params.set('difficulty', difficulty);
    if (minLength) params.set('minLength', minLength);
    if (maxLength) params.set('maxLength', maxLength);
    params.set('page', wordsAdminState.page);
    params.set('size', wordsAdminState.size);

    return params;
}

function resetWordSearch() {
    document.getElementById('wordSearchInput').value = '';
    document.getElementById('wordDifficultyFilter').value = '';
    document.getElementById('wordMinLength').value = '';
    document.getElementById('wordMaxLength').value = '';
    wordsAdminState.page = 0;
    loadAdminWords();
}

function changeWordsPage(delta) {
    const newPage = wordsAdminState.page + delta;
    if (newPage < 0 || newPage >= wordsAdminState.totalPages) return;
    wordsAdminState.page = newPage;
    loadAdminWords();
}

function updateWordsPaginationControls(currentPage) {
    const pageInput = document.getElementById('wordsPageInput');
    const displayPage = currentPage + 1;
    wordsPageInfo.textContent = `/ ${wordsAdminState.totalPages}`;
    pageInput.value = displayPage;
    pageInput.max = wordsAdminState.totalPages;
    pageInput.min = 1;
    document.getElementById('wordsPrevPageBtn').disabled = currentPage <= 0;
    document.getElementById('wordsNextPageBtn').disabled = currentPage >= wordsAdminState.totalPages - 1;
}

function goToWordsPage() {
    const pageInput = document.getElementById('wordsPageInput');
    const requestedPage = Number(pageInput.value);

    if (!Number.isInteger(requestedPage) || requestedPage < 1 || requestedPage > wordsAdminState.totalPages) {
        showWordsMessage(`Veuillez entrer une page entre 1 et ${wordsAdminState.totalPages}`, 'error');
        pageInput.value = wordsAdminState.page + 1;
        return;
    }

    if (requestedPage - 1 === wordsAdminState.page) {
        return;
    }

    wordsAdminState.page = requestedPage - 1;
    loadAdminWords();
}

async function loadAdminWords() {
    try {
        const params = buildWordSearchParams();
        const response = await adminFetch(`${API_URLS.game}/games/admin/words?${params.toString()}`);

        if (response.status === 401) {
            handleAdminLogout();
            adminLoginError.textContent = 'Session expirée, reconnectez-vous';
            openAdminLoginModal();
            return;
        }

        if (!response.ok) {
            throw new Error('Erreur lors du chargement des mots');
        }

        const data = await response.json();
        wordsAdminState.totalPages = Math.max(data.totalPages, 1);
        renderAdminWords(data);

        const filtered = data.totalElements;
        const total = data.totalDictionarySize;
        wordsTotalInfo.textContent = filtered === total
            ? `${total.toLocaleString('fr-FR')} mot(s) dans le dictionnaire`
            : `${filtered.toLocaleString('fr-FR')} mot(s) trouvé(s) sur ${total.toLocaleString('fr-FR')} au total`;
        updateWordsPaginationControls(data.page);

        if (filtered > 0) {
            showWordsMessage(`${filtered.toLocaleString('fr-FR')} mot(s) trouvé(s)`);
        } else {
            showWordsMessage('Aucun mot trouvé');
        }
    } catch (error) {
        showWordsMessage('Erreur: ' + error.message, 'error');
    }
}

function renderAdminWords(data) {
    adminWordsBody.innerHTML = '';
    const words = data.content || [];

    if (words.length === 0) {
        adminWordsBody.innerHTML = '<tr><td colspan="6" class="empty-row">Aucun mot</td></tr>';
        return;
    }

    words.forEach(word => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${word.id}</td>
            <td>${word.value ?? '-'}</td>
            <td>${word.language ?? '-'}</td>
            <td>${formatWordDifficulty(word.difficulty)}</td>
            <td>${word.length ?? (word.value ? word.value.length : '-')}</td>
            <td class="admin-actions">
                <button class="btn btn-secondary btn-small" data-action="edit-word" data-id="${word.id}">Modifier</button>
                <button class="btn btn-secondary btn-small btn-danger" data-action="delete-word" data-id="${word.id}">Supprimer</button>
            </td>
        `;
        adminWordsBody.appendChild(row);
    });

    adminWordsBody.querySelectorAll('button[data-action="edit-word"]').forEach(btn => {
        btn.addEventListener('click', () => openWordModal(btn.dataset.id));
    });
    adminWordsBody.querySelectorAll('button[data-action="delete-word"]').forEach(btn => {
        btn.addEventListener('click', () => deleteWord(btn.dataset.id));
    });
}

function setWordLanguageValue(language) {
    const select = document.getElementById('editWordLanguage');
    const normalized = (language || 'FR').trim().toUpperCase();
    select.value = normalized === 'EN' ? 'EN' : 'FR';
}

function openWordModal(id = null) {
    document.getElementById('editWordId').value = id || '';
    document.getElementById('editWordValue').value = '';
    setWordLanguageValue('FR');
    document.getElementById('editWordDifficulty').value = '';
    document.getElementById('adminWordError').textContent = '';
    document.getElementById('adminWordModalTitle').textContent = id ? 'Modifier le mot' : 'Ajouter un mot';
    adminWordModal.classList.remove('hidden');

    if (id) {
        loadWordForEdit(id);
    }
}

function closeWordModal() {
    adminWordModal.classList.add('hidden');
    document.getElementById('adminWordError').textContent = '';
}

async function loadWordForEdit(id) {
    try {
        const response = await adminFetch(`${API_URLS.game}/games/admin/words/${id}`);
        if (!response.ok) {
            throw new Error('Impossible de charger le mot');
        }
        const word = await response.json();
        document.getElementById('editWordId').value = word.id;
        document.getElementById('editWordValue').value = word.value ?? '';
        setWordLanguageValue(word.language ?? 'FR');
        document.getElementById('editWordDifficulty').value = word.difficulty ?? '';
    } catch (error) {
        closeWordModal();
        showWordsMessage('Erreur: ' + error.message, 'error');
    }
}

async function saveWord() {
    const id = document.getElementById('editWordId').value;
    const value = document.getElementById('editWordValue').value.trim();
    const language = document.getElementById('editWordLanguage').value.trim();
    const difficulty = document.getElementById('editWordDifficulty').value;
    const errorEl = document.getElementById('adminWordError');

    if (!value) {
        errorEl.textContent = 'Le mot est obligatoire';
        return;
    }

    const payload = { value, language: language || 'FR' };
    if (difficulty) payload.difficulty = difficulty;

    try {
        const url = id
            ? `${API_URLS.game}/games/admin/words/${id}`
            : `${API_URLS.game}/games/admin/words`;
        const response = await adminFetch(url, {
            method: id ? 'PUT' : 'POST',
            body: JSON.stringify(payload)
        });

        if (!response.ok) {
            const err = await response.json().catch(() => ({}));
            throw new Error(err.message || 'Erreur lors de l\'enregistrement');
        }

        closeWordModal();
        showWordsMessage(id ? 'Mot modifié avec succès' : 'Mot ajouté avec succès', 'success');
        await loadAdminWords();
    } catch (error) {
        errorEl.textContent = error.message;
    }
}

async function deleteWord(id) {
    if (!confirm('Supprimer ce mot du dictionnaire ?')) return;

    try {
        const response = await adminFetch(`${API_URLS.game}/games/admin/words/${id}`, {
            method: 'DELETE'
        });

        if (!response.ok) {
            const err = await response.json().catch(() => ({}));
            throw new Error(err.message || 'Erreur lors de la suppression');
        }

        showWordsMessage('Mot supprimé', 'success');
        await loadAdminWords();
    } catch (error) {
        showWordsMessage('Erreur: ' + error.message, 'error');
    }
}

// ============ Admin comptes ============

function buildUserSearchParams() {
    const params = new URLSearchParams();
    const search = document.getElementById('userSearchInput').value.trim();
    if (search) params.set('search', search);
    params.set('page', usersAdminState.page);
    params.set('size', usersAdminState.size);
    return params;
}

function resetUserSearch() {
    document.getElementById('userSearchInput').value = '';
    usersAdminState.page = 0;
    loadAdminUsers();
}

function changeUsersPage(delta) {
    const newPage = usersAdminState.page + delta;
    if (newPage < 0 || newPage >= usersAdminState.totalPages) return;
    usersAdminState.page = newPage;
    loadAdminUsers();
}

function updateUsersPaginationControls(currentPage) {
    const pageInput = document.getElementById('usersPageInput');
    usersPageInfo.textContent = `/ ${usersAdminState.totalPages}`;
    pageInput.value = currentPage + 1;
    pageInput.max = usersAdminState.totalPages;
    pageInput.min = 1;
    document.getElementById('usersPrevPageBtn').disabled = currentPage <= 0;
    document.getElementById('usersNextPageBtn').disabled = currentPage >= usersAdminState.totalPages - 1;
}

function goToUsersPage() {
    const pageInput = document.getElementById('usersPageInput');
    const requestedPage = Number(pageInput.value);

    if (!Number.isInteger(requestedPage) || requestedPage < 1 || requestedPage > usersAdminState.totalPages) {
        showUsersMessage(`Veuillez entrer une page entre 1 et ${usersAdminState.totalPages}`, 'error');
        pageInput.value = usersAdminState.page + 1;
        return;
    }

    if (requestedPage - 1 === usersAdminState.page) return;

    usersAdminState.page = requestedPage - 1;
    loadAdminUsers();
}

async function loadAdminUsers() {
    try {
        const params = buildUserSearchParams();
        const response = await adminFetch(`${API_URLS.player}/players/admin/users?${params.toString()}`);

        if (response.status === 401) {
            handleAdminLogout();
            adminLoginError.textContent = 'Session expirée, reconnectez-vous';
            openAdminLoginModal();
            return;
        }

        if (!response.ok) {
            throw new Error('Erreur lors du chargement des comptes');
        }

        const data = await response.json();
        usersAdminState.totalPages = Math.max(data.totalPages, 1);
        renderAdminUsers(data);

        const filtered = data.totalElements;
        const total = data.totalPlayers;
        usersTotalInfo.textContent = filtered === total
            ? `${total.toLocaleString('fr-FR')} compte(s) enregistré(s)`
            : `${filtered.toLocaleString('fr-FR')} compte(s) trouvé(s) sur ${total.toLocaleString('fr-FR')} au total`;
        updateUsersPaginationControls(data.page);

        if (filtered > 0) {
            showUsersMessage(`${filtered.toLocaleString('fr-FR')} compte(s) trouvé(s)`);
        } else {
            showUsersMessage('Aucun compte trouvé');
        }
    } catch (error) {
        showUsersMessage('Erreur: ' + error.message, 'error');
    }
}

function renderAdminUsers(data) {
    adminUsersBody.innerHTML = '';
    const users = data.content || [];

    if (users.length === 0) {
        adminUsersBody.innerHTML = '<tr><td colspan="5" class="empty-row">Aucun compte</td></tr>';
        return;
    }

    users.forEach(user => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${user.id}</td>
            <td>${user.username ?? '-'}</td>
            <td>${user.email ?? '-'}</td>
            <td>${user.registrationDate ? formatPlayedAt(user.registrationDate) : '-'}</td>
            <td class="admin-actions">
                <button class="btn btn-secondary btn-small" data-action="edit-user" data-id="${user.id}">Modifier</button>
                <button class="btn btn-secondary btn-small btn-danger" data-action="delete-user" data-id="${user.id}" data-username="${user.username ?? ''}">Supprimer</button>
            </td>
        `;
        adminUsersBody.appendChild(row);
    });

    adminUsersBody.querySelectorAll('button[data-action="edit-user"]').forEach(btn => {
        btn.addEventListener('click', () => openUserModal(btn.dataset.id));
    });
    adminUsersBody.querySelectorAll('button[data-action="delete-user"]').forEach(btn => {
        btn.addEventListener('click', () => deleteUser(btn.dataset.id, btn.dataset.username));
    });
}

function openUserModal(id = null) {
    const isEdit = Boolean(id);
    document.getElementById('editUserId').value = id || '';
    document.getElementById('editUserUsername').value = '';
    document.getElementById('editUserEmail').value = '';
    document.getElementById('editUserPassword').value = '';
    document.getElementById('editUserPasswordConfirm').value = '';
    document.getElementById('adminUserError').textContent = '';
    document.getElementById('adminUserModalTitle').textContent = isEdit ? 'Modifier le compte' : 'Créer un compte';
    document.getElementById('editUserPasswordLabelText').textContent = isEdit ? 'Nouveau mot de passe' : 'Mot de passe';
    document.getElementById('editUserPasswordConfirmLabel').textContent = isEdit ? 'Confirmer le nouveau mot de passe' : 'Confirmer le mot de passe';
    document.getElementById('editUserPasswordHint').classList.toggle('hidden', !isEdit);
    adminUserModal.classList.remove('hidden');

    if (isEdit) {
        loadUserForEdit(id);
    }
}

function closeUserModal() {
    adminUserModal.classList.add('hidden');
    document.getElementById('adminUserError').textContent = '';
}

async function loadUserForEdit(id) {
    try {
        const response = await adminFetch(`${API_URLS.player}/players/admin/users/${id}`);
        if (!response.ok) {
            throw new Error('Impossible de charger le compte');
        }
        const user = await response.json();
        document.getElementById('editUserId').value = user.id;
        document.getElementById('editUserUsername').value = user.username ?? '';
        document.getElementById('editUserEmail').value = user.email ?? '';
    } catch (error) {
        closeUserModal();
        showUsersMessage('Erreur: ' + error.message, 'error');
    }
}

async function saveUser() {
    const id = document.getElementById('editUserId').value;
    const username = document.getElementById('editUserUsername').value.trim();
    const email = document.getElementById('editUserEmail').value.trim();
    const password = document.getElementById('editUserPassword').value;
    const confirmPassword = document.getElementById('editUserPasswordConfirm').value;
    const errorEl = document.getElementById('adminUserError');
    const isEdit = Boolean(id);

    errorEl.textContent = '';

    if (!username) {
        errorEl.textContent = 'Le pseudo est obligatoire';
        return;
    }

    const emailError = validateEmail(email);
    if (emailError) {
        errorEl.textContent = emailError;
        return;
    }

    if (!isEdit) {
        const passwordError = validatePassword(password);
        if (passwordError) {
            errorEl.textContent = passwordError;
            return;
        }
        if (password !== confirmPassword) {
            errorEl.textContent = 'Les mots de passe ne correspondent pas';
            return;
        }
    } else if (password || confirmPassword) {
        const passwordError = validatePassword(password);
        if (passwordError) {
            errorEl.textContent = passwordError;
            return;
        }
        if (password !== confirmPassword) {
            errorEl.textContent = 'Les nouveaux mots de passe ne correspondent pas';
            return;
        }
    }

    const payload = { username, email };
    if (password) payload.password = password;

    try {
        const url = isEdit
            ? `${API_URLS.player}/players/admin/users/${id}`
            : `${API_URLS.player}/players/admin/users`;
        const response = await adminFetch(url, {
            method: isEdit ? 'PUT' : 'POST',
            body: JSON.stringify(payload)
        });

        if (!response.ok) {
            const err = await response.json().catch(() => ({}));
            throw new Error(err.message || 'Erreur lors de l\'enregistrement');
        }

        closeUserModal();
        showUsersMessage(isEdit ? 'Compte modifié avec succès' : 'Compte créé avec succès', 'success');
        await loadAdminUsers();
    } catch (error) {
        errorEl.textContent = error.message;
    }
}

async function deleteUser(id, username) {
    const label = username ? `le compte « ${username} »` : `le compte #${id}`;
    if (!confirm(`Supprimer ${label} ? Cette action est irréversible.`)) return;

    try {
        const response = await adminFetch(`${API_URLS.player}/players/admin/users/${id}`, {
            method: 'DELETE'
        });

        if (!response.ok) {
            const err = await response.json().catch(() => ({}));
            throw new Error(err.message || 'Erreur lors de la suppression');
        }

        showUsersMessage('Compte supprimé avec succès', 'success');
        await loadAdminUsers();
    } catch (error) {
        showUsersMessage('Erreur: ' + error.message, 'error');
    }
}
