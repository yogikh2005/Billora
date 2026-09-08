import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

import api from "../api/axios";
import "../css/invoice.css";


function Invoice() {

    const navigate = useNavigate();


    // =========================
    // Invoice State
    // =========================

    const [invoices, setInvoices] = useState([]);

    const [loading, setLoading] = useState(true);

    const [error, setError] = useState("");


    // =========================
    // Pagination
    // =========================

    const [page, setPage] = useState(0);

    const [size] = useState(5);

    const [totalPages, setTotalPages] = useState(0);


    // =========================
    // Popup
    // =========================

    const [popup, setPopup] = useState({
        show: false,
        type: "success",
        message: ""
    });


    // =========================
    // Show Popup
    // =========================

    const showPopup = (
        message,
        type = "success"
    ) => {

        setPopup({
            show: true,
            type,
            message
        });

    };


    // =========================
    // Close Popup
    // =========================

    const closePopup = () => {

        setPopup({
            show: false,
            type: "success",
            message: ""
        });

    };


    // =========================
    // Fetch Invoices
    // =========================

    const fetchInvoices = async () => {

        try {

            setLoading(true);

            setError("");


            const res = await api.get(
                `/invoices?page=${page}&size=${size}`
            );


            console.log(
                "Invoice Response:",
                res.data
            );


            setInvoices(
                res.data.content || []
            );


            setTotalPages(
                res.data.totalPages || 0
            );


        } catch (error) {

            console.error(error);


            setError(
                error.response?.data?.message ||
                "Failed to load invoices"
            );

        } finally {

            setLoading(false);

        }

    };


    useEffect(() => {

        fetchInvoices();

    }, [page]);


    // =========================
    // Add Invoice
    // =========================

    const handleAddInvoice = () => {

        navigate(
            "/invoice/create"
        );

    };


    // =========================
    // Update Invoice
    // =========================

    const handleUpdate = (id) => {

        navigate(
            `/invoice/edit/${id}`
        );

    };


    // =========================
    // Delete Invoice
    // =========================

    const handleDelete = async (
        id,
        invoiceNumber
    ) => {

        const confirmed =
            window.confirm(
                `Are you sure you want to delete invoice "${invoiceNumber}"?`
            );


        if (!confirmed) {
            return;
        }


        try {

            await api.delete(
                `/invoices/${id}`
            );


            showPopup(
                "Invoice deleted successfully"
            );


            fetchInvoices();


        } catch (error) {

            console.error(error);


            showPopup(

                error.response?.data?.message ||
                "Failed to delete invoice",

                "error"

            );

        }

    };


    // =========================
    // Format Currency
    // =========================

    const formatCurrency = (amount) => {

        return new Intl.NumberFormat(
            "en-IN",
            {
                style: "currency",
                currency: "INR",
                maximumFractionDigits: 2
            }
        ).format(amount || 0);

    };


    // =========================
    // Format Date
    // =========================

    const formatDate = (date) => {

        if (!date) {
            return "-";
        }

        return new Date(
            date
        ).toLocaleDateString(
            "en-IN"
        );

    };


    // =========================
    // Loading
    // =========================

    if (loading) {

        return (

            <div className="invoice-page">

                <div className="invoice-loading">

                    Loading invoices...

                </div>

            </div>

        );

    }

        
    // =========================
    // Page
    // =========================

    return (

        <div className="invoice-page">


            {/* =========================
                Header
            ========================= */}

            <div className="invoice-header">

                <div>

                    <h1>
                        Invoice Management
                    </h1>

                    <p>
                        Create and manage GST invoices
                    </p>

                </div>


                <button
                    className="add-invoice-button"
                    onClick={handleAddInvoice}
                >

                    + Create Invoice

                </button>

            </div>


            {/* Error */}

            {error && (

                <div className="invoice-error">

                    {error}

                </div>

            )}


            {/* =========================
                Table Card
            ========================= */}

            <div className="invoice-table-card">


                <div className="invoice-table-header">

                    <div>

                        <h2>
                            All Invoices
                        </h2>

                        <span>
                            {invoices.length} invoice
                            {invoices.length !== 1
                                ? "s"
                                : ""}
                        </span>

                    </div>

                </div>


                {invoices.length === 0 ? (

                    <div className="empty-invoices">

                        No invoices found.

                    </div>

                ) : (

                    <div className="invoice-table-wrapper">

                        <table className="invoice-table">


                            <thead>

                                <tr>

                                    <th>
                                        Invoice
                                    </th>

                                    <th>
                                        Business
                                    </th>

                                    <th>
                                        Customer
                                    </th>

                                    <th>
                                        Date
                                    </th>

                                    <th>
                                        Items
                                    </th>

                                    <th>
                                        Amount
                                    </th>

                                    <th>
                                        Status
                                    </th>

                                    <th>
                                        Actions
                                    </th>

                                </tr>

                            </thead>


                            <tbody>

                                {invoices.map(
                                    (invoice) => (

                                        <tr
                                            key={
                                                invoice.id
                                            }
                                        >


                                            {/* Invoice */}

                                            <td>

                                                <strong className="invoice-number">

                                                    #
                                                    {
                                                        invoice.invoiceNumber
                                                    }

                                                </strong>

                                            </td>


                                            {/* Business */}

                                            <td>

                                                <div className="invoice-business">

                                                    <div className="invoice-business-icon">

                                                        {
                                                            invoice.businessName
                                                                ?.charAt(0)
                                                                ?.toUpperCase()
                                                            || "B"
                                                        }

                                                    </div>


                                                    <span>

                                                        {
                                                            invoice.businessName
                                                            || "-"
                                                        }

                                                    </span>

                                                </div>

                                            </td>


                                            {/* Customer */}

                                            <td>

                                                <div className="invoice-customer">

                                                    <strong>
                                                        {invoice.customerName || "-"}
                                                    </strong> 
                                                </div>

                                            </td>


                                            {/* Date */}

                                            <td>

                                                {
                                                    formatDate(
                                                        invoice.invoiceDate
                                                    )
                                                }

                                            </td>


                                            {/* Items */}

                                            <td>

                                                {
                                                    invoice.items
                                                        ?.length
                                                    || 0
                                                }

                                            </td>


                                            {/* Amount */}

                                            <td>

                                                <strong>

                                                    {
                                                        formatCurrency(
                                                            invoice.totalAmount
                                                        )
                                                    }

                                                </strong>

                                            </td>


                                            {/* Status */}

                                            <td>

                                                <span
                                                    className={`invoice-status ${
                                                        (
                                                            invoice.status
                                                            || "UNPAID"
                                                        ).toLowerCase()
                                                    }`}
                                                >

                                                    {
                                                        invoice.status
                                                        || "UNPAID"
                                                    }

                                                </span>

                                            </td>


                                            {/* Actions */}

                                            <td>

                                                <div className="invoice-actions">


                                                    <button
                                                        className="invoice-view-button"
                                                        onClick={() => navigate(`/invoices/${invoice.id}/pdf`)}
                                                        >
                                                        View PDF
                                                    </button>


                                                    <button
                                                        className="invoice-update-button"
                                                        onClick={() =>
                                                            handleUpdate(
                                                                invoice.id
                                                            )
                                                        }
                                                    >
                                                        ✏️ Update
                                                    </button>


                                                    <button
                                                        className="invoice-delete-button"
                                                        onClick={() =>
                                                            handleDelete(
                                                                invoice.id,
                                                                invoice.invoiceNumber
                                                            )
                                                        }
                                                    >
                                                        🗑 Delete
                                                    </button>

                                                </div>

                                            </td>

                                        </tr>

                                    )
                                )}

                            </tbody>

                        </table>

                    </div>

                )}

            </div>


            {/* =========================
                Pagination
            ========================= */}

            {totalPages > 1 && (

                <div className="invoice-pagination">


                    <button
                        disabled={page === 0}
                        onClick={() =>
                            setPage(
                                page - 1
                            )
                        }
                    >
                        ← Previous
                    </button>


                    <span>

                        Page {page + 1}
                        {" "}
                        of
                        {" "}
                        {totalPages}

                    </span>


                    <button
                        disabled={
                            page >=
                            totalPages - 1
                        }
                        onClick={() =>
                            setPage(
                                page + 1
                            )
                        }
                    >
                        Next →
                    </button>

                </div>

            )}


            {/* =========================
                Popup
            ========================= */}

            {popup.show && (

                <div className="invoice-popup-overlay">

                    <div
                        className={`invoice-popup-box ${popup.type}`}
                    >

                        <div className="invoice-popup-icon">

                            {popup.type === "success"
                                ? "✓"
                                : "!"}

                        </div>


                        <h3>

                            {popup.type === "success"
                                ? "Success"
                                : "Error"}

                        </h3>


                        <p>
                            {popup.message}
                        </p>


                        <button
                            onClick={
                                closePopup
                            }
                        >
                            OK
                        </button>

                    </div>

                </div>

            )}

        </div>

    );

}


export default Invoice;