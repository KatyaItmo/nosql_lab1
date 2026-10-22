function Header({ isLoggedIn, userRole, switchPage, onLogout }) {
    return (
        <header>
            <h1>MOG fitness club</h1>
            <nav>
                <button className="tab" type="button" onClick={() => switchPage("main")}>
                    каталог
                </button>

                {isLoggedIn && userRole === 'user' && (
                    <button className="tab" type="button" onClick={() => switchPage("my-orders")}>
                        мои заказы
                    </button>
                )}

                {isLoggedIn && userRole === 'moder' && (
                    <button className="tab" type="button" onClick={() => switchPage("admin-tools")}>
                        управление заказами
                    </button>
                )}

                {isLoggedIn ? (
                    <button className="account-button" type="button" onClick={onLogout}>выйти</button>
                ) : (
                    <button className="account-button" type="button" onClick={() => switchPage("login")}>
                        войти
                    </button>
                )}
            </nav>
        </header>
    );
}

export default Header;