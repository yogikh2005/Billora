import { BrowserRouter, Routes, Route } from "react-router-dom";

import Login from "./pages/Login";
import Signup from "./pages/Signup";
import Dashboard from "./pages/Dashboard";
import Users from "./pages/Users";
import Business from "./pages/Business";
import Invoice from "./pages/Invoice";
import Reports from "./pages/Reports";
import UserForm from "./pages/UserForm";
import Layout from "./components/Layout";
import ProtectedRoute from "./components/ProtectedRoute";
import OwnerRoute from "./components/OwnerRoute";
import BusinessForm from "./pages/BusinessForm";
import "./css/dashboard.css";
import Setup from "./pages/Signup";
import InvoiceForm from "./pages/InvoiceForm";
import OwnerProfile from "./pages/OwnerProfile";
import InvoicePDF from "./pages/InvoicePDF";
import AuditLog from "./pages/AuditLog";


function App() {
    return (
        <BrowserRouter>

            <Routes>

                {/* =========================
                    Public Pages
                ========================= */}

                <Route
                    path="/login"
                    element={<Login />}
                />

                <Route
                    path="/"
                    element={<Setup />}
                />


                {/* =========================
                    All Logged-in Users
                ========================= */}

                <Route element={<ProtectedRoute />}>

                    {/* Dashboard */}
                    <Route
                        path="/dashboard"
                        element={
                            <Layout>
                                <Dashboard />
                            </Layout>
                        }
                    />

                    {/* Reports */}
                    <Route
                        path="/reports"
                        element={
                            <Layout>
                                <Reports />
                            </Layout>
                        }
                    />
                    <Route
                        path="/invoice"
                        element={
                            <Layout>
                                <Invoice />
                            </Layout>
                        }
                    />

                    <Route
                        path="/invoice/create"
                        element={
                            <Layout>
                                <InvoiceForm />
                            </Layout>
                        }
                    />

                    <Route
                        path="/invoice/edit/:id"
                        element={
                            <Layout>
                                <InvoiceForm />
                            </Layout>
                        }
                    />
                     <Route 
                         path="/invoices/:id/pdf"
                         element={<Layout>
                                <InvoicePDF/>
                            </Layout>} 
                    />
                    {/* =========================
                        OWNER ONLY
                    ========================= */}

                    <Route element={<OwnerRoute />}>
                        
                        <Route
                            path="/profile"
                            element={ <Layout><OwnerProfile/></Layout>}
                        />
                        {/* Users */}
                        <Route
                            path="/users"
                            element={
                                <Layout>
                                    <Users/>
                                </Layout>
                            }
                        />

                        {/* Add User */}
                        <Route
                            path="/users/add"
                            element={
                                <Layout>
                                    <UserForm />
                                </Layout>
                            }
                        />

                        {/* Edit User */}
                        <Route
                            path="/users/edit/:id"
                            element={
                                <Layout>
                                    <UserForm />
                                </Layout>
                            }
                        />

            
                        {/* Activity Logs */}
                        <Route
                            path="/logs"
                            element={
                                <Layout>
                                    <AuditLog/>
                                </Layout>
                            }
                        />
                        <Route
                            path="/business"
                            element={
                                <Layout>
                                    <Business />
                                </Layout>
                            }
                        />

                        {/* Add Business */}

                        <Route
                            path="/business/add"
                            element={
                                <Layout>
                                    <BusinessForm />
                                </Layout>
                            }
                        />

                        {/* Update Business */}

                        <Route
                            path="/business/edit/:id"
                            element={
                                <Layout>
                                    <BusinessForm />
                                </Layout>
                            }
                        />

                        
                    </Route>

                </Route>

            </Routes>

        </BrowserRouter>
    );
}

export default App;