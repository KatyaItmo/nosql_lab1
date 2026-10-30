function Header({ isLoggedIn, userRole, switchPage, onLogout }) {
    return (
        <header>
            <h1>MOG fitness club</h1>
            <div>
                <button type="button" onClick={() => switchPage("main")}>
                    каталог
                </button>

                {isLoggedIn && userRole === 'user' && (
                    <button type="button" onClick={() => switchPage("cart")}>
                        корзина
                    </button>
                )}

                {isLoggedIn && userRole === 'user' && (
                    <button type="button" onClick={() => switchPage("my-orders")}>
                        мои заказы
                    </button>
                )}

                {isLoggedIn && userRole === 'moder' && (
                    <button type="button" onClick={() => switchPage("moder-tools")}>
                        управление заказами
                    </button>
                )}

                {isLoggedIn ? (
                    <button type="button" onClick={onLogout}>выйти</button>
                ) : (
                    <button type="button" onClick={() => switchPage("login")}>
                        войти
                    </button>
                )}
            </div>
        </header>
    );
}

export default Header;