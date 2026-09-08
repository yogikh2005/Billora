import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import api from "../api/axios";

function InvoicePDF() {

    const { id } = useParams();

    const [pdfUrl, setPdfUrl] = useState("");
    const [loading, setLoading] = useState(true);

    useEffect(() => {

        const loadPdf = async () => {

            try {

                const response = await api.get(
                    `/invoices/${id}/pdf`,
                    {
                        responseType: "blob"
                    }
                );

                const url = window.URL.createObjectURL(
                    new Blob(
                        [response.data],
                        {
                            type: "application/pdf"
                        }
                    )
                );

                setPdfUrl(url);

            } catch (error) {

                console.error(
                    "Failed to load invoice PDF",
                    error
                );

            } finally {

                setLoading(false);
            }
        };

        loadPdf();


        return () => {

            if (pdfUrl) {
                window.URL.revokeObjectURL(pdfUrl);
            }

        };

    }, [id]);



    if (loading) {
        return <p>Loading invoice...</p>;
    }


    if (!pdfUrl) {
        return <p>Unable to load invoice PDF.</p>;
    }


    return (
        <div
            style={{
                width: "100%",
                height: "calc(100vh - 80px)"
            }}
        >

            <iframe
                src={pdfUrl}
                title={`Invoice ${id}`}
                style={{
                    width: "100%",
                    height: "100%",
                    border: "none"
                }}
            />

        </div>
    );
}

export default InvoicePDF;