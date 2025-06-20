import React from "react";
import "../styles/StockWarning.css";

const StockWarning = ({
  type = "warning", // 'warning', 'danger', 'info'
  title,
  message,
  suggestions = [],
  onAction,
  actionText = "Entendido",
  showAction = true,
  autoHide = false,
  duration = 5000,
  onClose,
}) => {
  React.useEffect(() => {
    if (autoHide && onClose) {
      const timer = setTimeout(() => {
        onClose();
      }, duration);
      return () => clearTimeout(timer);
    }
  }, [autoHide, duration, onClose]);

  const getIcon = () => {
    switch (type) {
      case "danger":
        return "🚫";
      case "warning":
        return "⚠️";
      case "info":
        return "ℹ️";
      default:
        return "⚠️";
    }
  };

  const getTypeClass = () => {
    return `stock-warning-${type}`;
  };

  return (
    <div className={`stock-warning ${getTypeClass()}`}>
      <div className="stock-warning-header">
        <div className="stock-warning-icon">{getIcon()}</div>
        <div className="stock-warning-title">{title}</div>
        {onClose && (
          <button
            className="stock-warning-close"
            onClick={onClose}
            aria-label="Cerrar">
            ✕
          </button>
        )}
      </div>

      <div className="stock-warning-content">
        <p className="stock-warning-message">{message}</p>

        {suggestions.length > 0 && (
          <div className="stock-warning-suggestions">
            <strong>💡 Sugerencias:</strong>
            <ul>
              {suggestions.map((suggestion, index) => (
                <li key={index}>{suggestion}</li>
              ))}
            </ul>
          </div>
        )}
      </div>

      {showAction && onAction && (
        <div className="stock-warning-actions">
          <button className="stock-warning-action-btn" onClick={onAction}>
            {actionText}
          </button>
        </div>
      )}
    </div>
  );
};

export default StockWarning;
