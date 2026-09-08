import SideBar from "./SideBar";
import Navbar from "./Navbar";

function Layout({ children }) {

    return (
        <div className="app">

            <SideBar />

            <div className="main">

                <Navbar />

                <main className="content">
                    {children}
                </main>
                
            </div>

        </div>
    );
}

export default Layout;
