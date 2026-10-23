import {useState} from "react";

function LoginPage({ onLoginSuccess, showMessage }) {
    const [isRegistering, setIsRegistering] = useState(false);

    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [isModer, setIsModer] = useState(false);


    const submitForm = async () => {
        if (username === '' || password === '') {
            showMessage('заполните все поля!', 'error');
            return;
        }

        const formData = {
            role: (isModer ? 'moder' : 'user'),
            username: username,
            password: password
        }

        const url = isRegistering
            ? '/api/auth/register'
            : '/api/auth/login';

        try {
            const response = await fetch(url, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                credentials: 'include',
                body: JSON.stringify(formData)
            });

            const resultText = await response.text();

            if (response.ok) {
                showMessage(resultText, 'info');

                if (!isRegistering) {
                    onLoginSuccess(formData.role);
                }

            } else {
                showMessage(resultText || 'ошибка сервера', 'error');
            }
        } catch (err) {
            showMessage('не удалось связаться с сервером', 'error');
        }


    };

    return (
        <div className="page">
            <h2>{isRegistering ? 'регистрация' : 'вход'}</h2>

            <form className="login-form">
                <input
                    className="login-input"
                    type="text"
                    placeholder="введите логин"
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                />

                <input
                    className="login-input"
                    type="password"
                    placeholder="введите пароль"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                />

                <label>
                    <input
                        type="checkbox"
                        checked={isModer}
                        onChange={(e) => setIsModer(e.target.checked)}
                    />
                    я модератор
                </label>

                <button
                    className="simple-button"
                    type="button"
                    onClick={() => submitForm()}
                >
                    {isRegistering ? 'зарегистрироваться' : 'войти'}
                </button>
            </form>

            <button className="login-mode-button" type="button" onClick={() => setIsRegistering(!isRegistering)}>
                {isRegistering ? 'есть аккаунт? войти' : 'нет аккаунта? зарегистрироваться'}
            </button>
        </div>
    );
}

export default LoginPage;