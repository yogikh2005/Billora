import { useEffect, useState } from "react";
import {
    useNavigate,
    useParams
} from "react-router-dom";

import api from "../api/axios";
import "../css/invoice.css";


function InvoiceForm() {

    const navigate = useNavigate();

    const { id } = useParams();

    const isEditMode = Boolean(id);


    // =========================
    // Business
    // =========================

    const [businesses, setBusinesses] =
        useState([]);


    // =========================
    // Form
    // =========================

    const [formData, setFormData] =
        useState({

            businessId: "",

            customerName: "",
            customerEmail: "",
            customerAddress: "",
            customerGstNo: "",
            customerPhone: "",

            invoiceDate:
                new Date()
                    .toISOString()
                    .split("T")[0],

            status: "UNPAID",

            items: [
                {
                    itemName: "",
                    quantity: 1,
                    price: 0,
                    hsn: "",
                    gstRate: 18
                }
            ]

        });


    // =========================
    // States
    // =========================

    const [loading, setLoading] =
        useState(false);

    const [saving, setSaving] =
        useState(false);

    const [error, setError] =
        useState("");


    // =========================
    // Popup
    // =========================

    const [popup, setPopup] =
        useState({

            show: false,

            type: "success",

            message: ""

        });


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


    const closePopup = () => {

        setPopup({

            show: false,

            type: "success",

            message: ""

        });


        navigate("/invoice");

    };


    // =========================
    // Fetch Businesses
    // =========================

    const fetchBusinesses = async () => {

        try {

            const res =
                await api.get(
                    "/business"
                );
            

            setBusinesses(
                res.data || []
            );

        } catch (error) {

            console.error(error);

            setError(
                "Failed to load businesses"
            );

        }

    };


    // =========================
    // Fetch Invoice
    // =========================

    const fetchInvoice = async () => {

        if (!isEditMode) {
            return;
        }


        try {

            setLoading(true);


            const res =
                await api.get(
                    `/invoices/${id}`
                );
            console.log("get method claaed+");
            console.log(res);

            const invoice =
                res.data;


            setFormData({

                businessId:
                    invoice.businessId
                    || "",

                customerName:
                    invoice.customerName 
                    || "",

                customerEmail:
                    invoice.customerEmail
                    || "",

                customerAddress:
                    invoice.customerAddress
                    || "",

                customerGstNo:
                    invoice.customerGstin
                    || "",

                customerPhone:
                    invoice.customerPhone
                    || "",

                invoiceDate:
                    invoice.invoiceDate
                    || "",

                status:
                    invoice.status
                    || "UNPAID",

                items:
                    invoice.items?.map(
                        (item) => ({

                            itemName:
                                item.itemName
                                || "",

                            quantity:
                                item.quantity
                                || 1,

                            price:
                                item.price
                                || 0,

                            hsn:
                                item.hsn
                                || "",

                            gstRate:
                                item.gstRate
                                || 18

                        })
                    ) || []

            });


        } catch (error) {

            console.error(error);


            setError(
                error.response?.data?.message ||
                "Failed to load invoice"
            );

        } finally {

            setLoading(false);

        }

    };


    // =========================
    // Initial Load
    // =========================

    useEffect(() => {

        fetchBusinesses();

        fetchInvoice();

    }, [id]);


    // =========================
    // Input Change
    // =========================

    const handleChange = (e) => {

        const {
            name,
            value
        } = e.target;


        setFormData({

            ...formData,

            [name]: value

        });

    };


    // =========================
    // Item Change
    // =========================

    const handleItemChange = (
        index,
        field,
        value
    ) => {

        const updatedItems =
            [...formData.items];


        updatedItems[index] = {

            ...updatedItems[index],

            [field]: value

        };


        setFormData({

            ...formData,

            items: updatedItems

        });

    };


    // =========================
    // Add Item
    // =========================

    const addItem = () => {

        setFormData({

            ...formData,

            items: [

                ...formData.items,

                {
                    itemName: "",
                    quantity: 1,
                    price: 0,
                    hsn: "",
                    gstRate: 18
                }

            ]

        });

    };


    // =========================
    // Remove Item
    // =========================

    const removeItem = (index) => {

        if (
            formData.items.length === 1
        ) {

            return;

        }


        const updatedItems =
            formData.items.filter(
                (_, i) =>
                    i !== index
            );


        setFormData({

            ...formData,

            items: updatedItems

        });

    };


    // =========================
    // Calculate Subtotal
    // =========================

    const calculateSubtotal = () => {

        return formData.items.reduce(
            (total, item) => {

                const quantity =
                    Number(
                        item.quantity
                    ) || 0;


                const price =
                    Number(
                        item.price
                    ) || 0;


                return (
                    total +
                    quantity * price
                );

            },
            0
        );

    };


    // =========================
    // Calculate GST
    // =========================

    const calculateGST = () => {

        return formData.items.reduce(
            (total, item) => {

                const quantity =
                    Number(
                        item.quantity
                    ) || 0;


                const price =
                    Number(
                        item.price
                    ) || 0;


                const gstRate =
                    Number(
                        item.gstRate
                    ) || 0;


                const subtotal =
                    quantity * price;


                return (
                    total +
                    subtotal *
                    gstRate /
                    100
                );

            },
            0
        );

    };


    const subtotal =
        calculateSubtotal();


    const gstAmount =
        calculateGST();


    const totalAmount =
        subtotal + gstAmount;


    // =========================
    // Submit
    // =========================

    const handleSubmit = async (
        e
    ) => {

        e.preventDefault();


        try {

            setSaving(true);

            setError("");


            const requestData = {

                ...formData,

                businessId:
                    Number(
                        formData.businessId
                    ),

                items:
                    formData.items.map(
                        (item) => ({

                            itemName:
                                item.itemName,

                            quantity:
                                Number(
                                    item.quantity
                                ),

                            price:
                                Number(
                                    item.price
                                ),

                            hsn:
                                item.hsn
                                    ? Number(
                                        item.hsn
                                    )
                                    : null,

                            gstRate:
                                Number(
                                    item.gstRate
                                )

                        })
                    )

            };


            if (isEditMode) {

                await api.put(

                    `/invoices/${id}`,

                    requestData

                );


                showPopup(
                    "Invoice updated successfully"
                );

            } else {

                await api.post(

                    "/invoices",

                    requestData

                );


                showPopup(
                    "Invoice created successfully"
                );

            }


        } catch (error) {

            console.error(error);


            console.log(
                error.response?.data
            );


            showPopup(

                error.response?.data?.message ||
                "Failed to save invoice",

                "error"

            );

        } finally {

            setSaving(false);

        }

    };


    // =========================
    // Loading
    // =========================

    if (loading) {

        return (

            <div className="invoice-form-page">

                <div className="invoice-loading">

                    Loading invoice...

                </div>

            </div>

        );

    }


    // =========================
    // Page
    // =========================

    return (

        <div className="invoice-form-page">


            {/* Header */}

            <div className="invoice-form-header">

                <div>

                    <h1>

                        {isEditMode
                            ? "Update Invoice"
                            : "Create Invoice"}

                    </h1>

                    <p>

                        {isEditMode
                            ? "Update invoice details"
                            : "Create a new GST invoice"}

                    </p>

                </div>

            </div>


            {/* Error */}

            {error && (

                <div className="invoice-error">

                    {error}

                </div>

            )}


            <form
                className="invoice-form-card"
                onSubmit={handleSubmit}
            >


                {/* =========================
                    Invoice Information
                ========================= */}

                <div className="invoice-section">

                    <h3>
                        Invoice Information
                    </h3>


                    <div className="invoice-form-grid">


                        {/* Business */}

                        <div className="invoice-form-group">

                            <label>
                                Business
                            </label>


                            <select
                                name="businessId"
                                value={
                                    formData.businessId
                                }
                                onChange={
                                    handleChange
                                }
                                required
                            >

                                <option value="">
                                    Select Business
                                </option>


                                {businesses.map(
                                    (business) => (

                                        <option
                                            key={
                                                business.id
                                            }
                                            value={
                                                business.id
                                            }
                                        >

                                            {
                                                business.businessName
                                            }

                                        </option>

                                    )
                                )}

                            </select>

                        </div>


                        {/* Date */}

                        <div className="invoice-form-group">

                            <label>
                                Invoice Date
                            </label>


                            <input
                                type="date"
                                name="invoiceDate"
                                value={
                                    formData.invoiceDate
                                }
                                onChange={
                                    handleChange
                                }
                                required
                            />

                        </div>


                        {/* Status */}

                        <div className="invoice-form-group">

                            <label>
                                Status
                            </label>


                            <select
                                name="status"
                                value={
                                    formData.status
                                }
                                onChange={
                                    handleChange
                                }
                            >

                                <option value="UNPAID">
                                    Unpaid
                                </option>

                                <option value="PAID">
                                    Paid
                                </option>

                                <option value="SENT">
                                    Sent
                                </option>

                                <option value="DRAFT">
                                    Draft
                                </option>

                            </select>

                        </div>

                    </div>

                </div>


                {/* =========================
                    Customer Information
                ========================= */}

                <div className="invoice-section">

                    <h3>
                        Customer Information
                    </h3>


                    <div className="invoice-form-grid">


                        <div className="invoice-form-group">

                            <label>
                                Customer Name
                            </label>


                            <input
                                type="text"
                                name="customerName"
                                value={
                                    formData.customerName
                                }
                                onChange={
                                    handleChange
                                }
                                placeholder="Customer name"
                                required
                            />

                        </div>


                        <div className="invoice-form-group">

                            <label>
                                Customer Email
                            </label>


                            <input
                                type="email"
                                name="customerEmail"
                                value={
                                    formData.customerEmail
                                }
                                onChange={
                                    handleChange
                                }
                                placeholder="customer@example.com"
                            />

                        </div>


                        <div className="invoice-form-group">

                            <label>
                                Phone
                            </label>


                            <input
                                type="text"
                                name="customerPhone"
                                value={
                                    formData.customerPhone
                                }
                                onChange={
                                    handleChange
                                }
                                placeholder="Phone number"
                            />

                        </div>


                        <div className="invoice-form-group">

                            <label>
                                GSTIN
                            </label>


                            <input
                                type="text"
                                name="customerGstNo"
                                value={
                                    formData.customerGstNo
                                }
                                onChange={
                                    handleChange
                                }
                                placeholder="Customer GSTIN"
                            />

                        </div>


                        <div className="invoice-form-group full">

                            <label>
                                Address
                            </label>


                            <textarea
                                name="customerAddress"
                                value={
                                    formData.customerAddress
                                }
                                onChange={
                                    handleChange
                                }
                                placeholder="Customer address"
                                rows="3"
                            />

                        </div>

                    </div>

                </div>


                {/* =========================
                    Items
                ========================= */}

                <div className="invoice-section">

                    <div className="invoice-items-header">

                        <div>

                            <h3>
                                Invoice Items
                            </h3>

                            <p>
                                Add products or services
                            </p>

                        </div>


                        <button
                            type="button"
                            className="add-item-button"
                            onClick={addItem}
                        >
                            + Add Item
                        </button>

                    </div>


                    <div className="invoice-items-wrapper">

                        <table className="invoice-items-table">

                            <thead>

                                <tr>

                                    <th>
                                        Item
                                    </th>

                                    <th>
                                        HSN
                                    </th>

                                    <th>
                                        Qty
                                    </th>

                                    <th>
                                        Price
                                    </th>

                                    <th>
                                        GST %
                                    </th>

                                    <th>
                                        Amount
                                    </th>

                                    <th>
                                    </th>

                                </tr>

                            </thead>


                            <tbody>

                                {formData.items.map(
                                    (
                                        item,
                                        index
                                    ) => {

                                        const itemSubtotal =
                                            (
                                                Number(
                                                    item.quantity
                                                ) || 0
                                            ) *
                                            (
                                                Number(
                                                    item.price
                                                ) || 0
                                            );


                                        const itemGST =
                                            itemSubtotal *
                                            (
                                                Number(
                                                    item.gstRate
                                                ) || 0
                                            ) /
                                            100;


                                        const itemTotal =
                                            itemSubtotal +
                                            itemGST;


                                        return (

                                            <tr
                                                key={
                                                    index
                                                }
                                            >


                                                <td>

                                                    <input
                                                        type="text"
                                                        value={
                                                            item.itemName
                                                        }
                                                        onChange={
                                                            (e) =>
                                                                handleItemChange(
                                                                    index,
                                                                    "itemName",
                                                                    e.target.value
                                                                )
                                                        }
                                                        placeholder="Item name"
                                                        required
                                                    />

                                                </td>


                                                <td>

                                                    <input
                                                        type="number"
                                                        value={
                                                            item.hsn
                                                        }
                                                        onChange={
                                                            (e) =>
                                                                handleItemChange(
                                                                    index,
                                                                    "hsn",
                                                                    e.target.value
                                                                )
                                                        }
                                                        placeholder="HSN"
                                                    />

                                                </td>


                                                <td>

                                                    <input
                                                        type="number"
                                                        min="1"
                                                        value={
                                                            item.quantity
                                                        }
                                                        onChange={
                                                            (e) =>
                                                                handleItemChange(
                                                                    index,
                                                                    "quantity",
                                                                    e.target.value
                                                                )
                                                        }
                                                        required
                                                    />

                                                </td>


                                                <td>

                                                    <input
                                                        type="number"
                                                        min="0"
                                                        step="0.01"
                                                        value={
                                                            item.price
                                                        }
                                                        onChange={
                                                            (e) =>
                                                                handleItemChange(
                                                                    index,
                                                                    "price",
                                                                    e.target.value
                                                                )
                                                        }
                                                        required
                                                    />

                                                </td>


                                                <td>

                                                    <input
                                                        type="number"
                                                        min="0"
                                                        max="100"
                                                        step="0.01"
                                                        value={
                                                            item.gstRate
                                                        }
                                                        onChange={
                                                            (e) =>
                                                                handleItemChange(
                                                                    index,
                                                                    "gstRate",
                                                                    e.target.value
                                                                )
                                                        }
                                                    />

                                                </td>


                                                <td>

                                                    <strong>

                                                        ₹
                                                        {itemTotal.toFixed(
                                                            2
                                                        )}

                                                    </strong>

                                                </td>


                                                <td>

                                                    <button
                                                        type="button"
                                                        className="remove-item-button"
                                                        onClick={() =>
                                                            removeItem(
                                                                index
                                                            )
                                                        }
                                                    >
                                                        ✕
                                                    </button>

                                                </td>

                                            </tr>

                                        );

                                    }
                                )}

                            </tbody>

                        </table>

                    </div>

                </div>


                {/* =========================
                    Totals
                ========================= */}

                <div className="invoice-total-section">

                    <div className="invoice-total-row">

                        <span>
                            Subtotal
                        </span>

                        <strong>
                            ₹
                            {subtotal.toFixed(2)}
                        </strong>

                    </div>


                    <div className="invoice-total-row">

                        <span>
                            GST
                        </span>

                        <strong>
                            ₹
                            {gstAmount.toFixed(2)}
                        </strong>

                    </div>


                    <div className="invoice-total-row grand-total">

                        <span>
                            Total Amount
                        </span>

                        <strong>
                            ₹
                            {totalAmount.toFixed(2)}
                        </strong>

                    </div>

                </div>


                {/* =========================
                    Buttons
                ========================= */}

                <div className="invoice-form-actions">

                    <button
                        type="button"
                        className="invoice-cancel-button"
                        onClick={() =>
                            navigate("/invoice")
                        }
                        disabled={saving}
                    >
                        Cancel
                    </button>


                    <button
                        type="submit"
                        className="invoice-save-button"
                        disabled={saving}
                    >

                        {saving
                            ? "Saving..."
                            : isEditMode
                                ? "Update Invoice"
                                : "Create Invoice"}

                    </button>

                </div>

            </form>


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
                            type="button"
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


export default InvoiceForm; 