import Modal from "@mui/material/Modal";
import Paper from "@mui/material/Paper";

function CustomModal({ isOpen, closeModal, children }) {
    return (
        <Modal open={isOpen} onClose={closeModal}>
            <Paper
                elevation={2}
                sx={{
                    position: "absolute",
                    top: "50%",
                    left: "50%",
                    transform: "translate(-50%, -50%)",
                    width: "auto",
                    maxHeight: "90%",
                    maxWidth: "calc(100% - 32px)",
                    overflowY: "auto",
                    padding: "16px",
                    textAlign: "center",
                }}
            >
                {children}
            </Paper>
        </Modal>
    );
}

export default CustomModal;