import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

import api from "../api/axios";
import "../css/business.css";

function Business() {

    const navigate = useNavigate();

    const [businesses, setBusinesses] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

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
    // Fetch Businesses
    // =========================

    const fetchBusinesses = async () => {

        try {

            setLoading(true);
            setError("");

            const res = await api.get("/business");

            console.log(
                "Businesses:",
                res.data
            );

            setBusinesses(res.data);

        } catch (error) {

            console.error(error);

            setError(
                error.response?.data?.message ||
                "Failed to load businesses"
            );

        } finally {

            setLoading(false);

        }
    };


    // =========================
    // Load Businesses
    // =========================

    useEffect(() => {

        fetchBusinesses();

    }, []);


    // =========================
    // Add Business
    // =========================

    const handleAddBusiness = () => {

        navigate("/business/add");

    };


    // =========================
    // Update Business
    // =========================

    const handleUpdate = (id) => {

        navigate(`/business/edit/${id}`);

    };


    // =========================
    // Activate / Deactivate
    // =========================

    const handleToggleStatus = async (business) => {

        const newStatus = !business.active;

        const action = newStatus
            ? "activate"
            : "deactivate";


        const confirmed = window.confirm(
            `Are you sure you want to ${action} "${business.businessName}"?`
        );


        if (!confirmed) {
            return;
        }


        try {

            await api.patch(
                `/business/${business.id}/status`,
                {
                    active: newStatus
                }
            );


            showPopup(
                `Business ${
                    newStatus
                        ? "activated"
                        : "deactivated"
                } successfully`
            );


            // Refresh list
            fetchBusinesses();


        } catch (error) {

            console.error(error);

            console.log(
                error.response?.data
            );


            showPopup(
                error.response?.data?.message ||
                `Failed to ${action} business`,
                "error"
            );
        }
    };


    // =========================
    // Loading
    // =========================

    if (loading) {

        return (

            <div className="business-page">

                <div className="business-loading">

                    Loading businesses...

                </div>

            </div>

        );

    }


    // =========================
    // Page
    // =========================

    return (

        <div className="business-page">


            {/* =========================
                Header
            ========================= */}

            <div className="business-header">

                <div>

                    <h1>
                        Business Management
                    </h1>

                    <p>
                        Manage registered businesses
                        and their information
                    </p>

                </div>


                <button
                    className="add-business-button"
                    onClick={handleAddBusiness}
                >
                    + Add Business
                </button>

            </div>


            {/* =========================
                Error
            ========================= */}

            {error && (

                <div className="business-error">

                    {error}

                </div>

            )}


            {/* =========================
                Business Table
            ========================= */}

            <div className="business-table-card">


                {/* Table Header */}

                <div className="business-table-header">

                    <div>

                        <h2>
                            All Businesses
                        </h2>

                        <span>

                            {businesses.length} business
                            {businesses.length !== 1
                                ? "es"
                                : ""
                            }

                        </span>

                    </div>

                </div>


                {/* =========================
                    Empty
                ========================= */}

                {businesses.length === 0 ? (

                    <div className="empty-business">

                        No businesses found.

                    </div>

                ) : (

                    <div className="business-table-wrapper">

                        <table className="business-table">


                            {/* =========================
                                Table Head
                            ========================= */}

                            <thead>

                                <tr>

                                    <th>ID</th>

                                    <th>Business</th>

                                    <th>GSTIN</th>

                                    <th>Contact</th>

                                    <th>Location</th>

                                    <th>Bank</th>

                                    <th>Website</th>

                                    <th>Status</th>

                                    <th>Actions</th>

                                </tr>

                            </thead>


                            {/* =========================
                                Table Body
                            ========================= */}

                            <tbody>

                                {businesses.map(
                                    (business) => (

                                    <tr
                                        key={business.id}
                                        className={
                                            business.active
                                                ? ""
                                                : "business-inactive-row"
                                        }
                                    >


                                        {/* ID */}

                                        <td>

                                            #{business.id}

                                        </td>


                                        {/* Business */}

                                        <td>

                                            <div className="business-name">

                                                <div className="business-logo">

                                                    {business.businessName
                                                        ?.charAt(0)
                                                        .toUpperCase()
                                                    }

                                                </div>


                                                <strong>

                                                    {business.businessName}

                                                </strong>

                                            </div>

                                        </td>


                                        {/* GSTIN */}

                                        <td>

                                            <span className="gstin">

                                                {business.gstin || "-"}

                                            </span>

                                        </td>


                                        {/* Contact */}

                                        <td>

                                            <div className="contact-info">

                                                <span>

                                                    📧{" "}
                                                    {business.email || "-"}

                                                </span>


                                                <span>

                                                    📱{" "}
                                                    {business.mobileNo || "-"}

                                                </span>

                                            </div>

                                        </td>


                                        {/* Location */}

                                        <td>

                                            <div className="location-info">

                                                <strong>

                                                    {business.city || "-"}

                                                </strong>


                                                <span>

                                                    {business.state || "-"}

                                                </span>


                                                <span>

                                                    {business.pincode || ""}

                                                </span>

                                            </div>

                                        </td>


                                        {/* Bank */}

                                        <td>

                                            <div className="bank-info">

                                                <strong>

                                                    {business.bankName || "-"}

                                                </strong>


                                                <span>

                                                    {business.branch || "-"}

                                                </span>


                                                <span>

                                                    A/C ****
                                                    {business.accountNumber
                                                        ? business.accountNumber.slice(-4)
                                                        : ""
                                                    }

                                                </span>

                                            </div>

                                        </td>


                                        {/* Website */}

                                        <td>

                                            {business.website ? (

                                                <a
                                                    href={
                                                        business.website.startsWith(
                                                            "http"
                                                        )
                                                            ? business.website
                                                            : `https://${business.website}`
                                                    }
                                                    target="_blank"
                                                    rel="noopener noreferrer"
                                                    className="website-link"
                                                >

                                                    Visit

                                                </a>

                                            ) : (

                                                "-"

                                            )}

                                        </td>


                                        {/* Status */}

                                        <td>

                                            <span
                                                className={
                                                    business.active
                                                        ? "business-status active"
                                                        : "business-status inactive"
                                                }
                                            >

                                                {business.active
                                                    ? "Active"
                                                    : "Inactive"
                                                }

                                            </span>

                                        </td>


                                        {/* Actions */}

                                        <td>

                                            <div className="business-actions">


                                                {/* Update */}

                                                <button
                                                    type="button"
                                                    className="business-update-button"
                                                    onClick={() =>
                                                        handleUpdate(
                                                            business.id
                                                        )
                                                    }
                                                >

                                                    ✏️ Update

                                                </button>


                                                {/* Activate / Deactivate */}

                                                <button
                                                    type="button"
                                                    className={
                                                        business.active
                                                            ? "business-deactivate-button"
                                                            : "business-activate-button"
                                                    }
                                                    onClick={() =>
                                                        handleToggleStatus(
                                                            business
                                                        )
                                                    }
                                                >

                                                    {business.active
                                                        ? "⏸️ Deactivate"
                                                        : "▶️ Activate"
                                                    }

                                                </button>

                                            </div>

                                        </td>

                                    </tr>

                                ))}

                            </tbody>

                        </table>

                    </div>

                )}

            </div>


            {/* =========================
                Success / Error Popup
            ========================= */}

            {popup.show && (

                <div className="popup-overlay">

                    <div
                        className={`popup-box ${popup.type}`}
                    >


                        <div className="popup-icon">

                            {popup.type === "success"
                                ? "✓"
                                : "!"
                            }

                        </div>


                        <h3>

                            {popup.type === "success"
                                ? "Success"
                                : "Error"
                            }

                        </h3>


                        <p>

                            {popup.message}

                        </p>


                        <button
                            type="button"
                            className="popup-button"
                            onClick={closePopup}
                        >

                            OK

                        </button>

                    </div>

                </div>

            )}

        </div>

    );
}


export default Business;