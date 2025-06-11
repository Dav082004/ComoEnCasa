import React, { useEffect, useState } from "react";
import "../styles/Admin.css";
import { getAllPedidos } from "../services/pedidoService";

function AdminOrders() {
  const [orders, setOrders] = useState([]);
  const [filteredOrders, setFilteredOrders] = useState([]);
  const [searchTerm, setSearchTerm] = useState("");
  const [selectedOrder, setSelectedOrder] = useState(null);
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchOrders();
  }, []);

  const fetchOrders = async () => {
    try {
      setLoading(true);
      const data = await getAllPedidos();
      setOrders(data);
      setFilteredOrders(data);
    } catch (error) {
      console.error("Error al cargar los pedidos:", error);
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = () => {
    const filtered = orders.filter(
      (order) =>
        order.id?.toString().includes(searchTerm) ||
        order.usuarioNombre?.toLowerCase().includes(searchTerm.toLowerCase()) ||
        order.estado?.toLowerCase().includes(searchTerm.toLowerCase())
    );
    setFilteredOrders(filtered);
  };

  const handleDelete = async () => {
    // TODO: Implementar eliminación de pedidos si es necesario
    alert(
      "La eliminación de pedidos requiere implementación adicional en el backend"
    );
    setShowDeleteModal(false);
  };

  const formatDate = (dateString) => {
    if (!dateString) return "N/A";
    return new Date(dateString).toLocaleDateString("es-ES");
  };

  const formatCurrency = (amount) => {
    if (!amount) return "S/ 0.00";
    return `S/ ${Number(amount).toFixed(2)}`;
  };

  return (
    <div className="page-container">
      <h2 className="theme-header">Gestión de Pedidos</h2>

      <div className="admin-header">
        <input
          type="text"
          placeholder="Buscar por ID, cliente o estado..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
        />
        <button className="theme-button" onClick={handleSearch}>
          Buscar
        </button>
      </div>

      {loading ? (
        <div
          className="loading-container"
          style={{ padding: "2rem", textAlign: "center" }}>
          <div className="spinner-border text-pink" role="status">
            <span className="visually-hidden">Cargando...</span>
          </div>
          <p>Cargando pedidos...</p>
        </div>
      ) : (
        <>
          <table className="admin-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Cliente</th>
                <th>Fecha Creación</th>
                <th>Fecha Entrega</th>
                <th>Estado</th>
                <th>Total</th>
                <th>Dirección</th>
              </tr>
            </thead>
            <tbody>
              {filteredOrders.map((order) => (
                <tr
                  key={order.id}
                  onClick={() => setSelectedOrder(order)}
                  className={
                    selectedOrder?.id === order.id ? "selected-row" : ""
                  }>
                  <td>{order.id}</td>
                  <td>{order.usuarioNombre || "N/A"}</td>
                  <td>{formatDate(order.fechaCreacion)}</td>
                  <td>{formatDate(order.fechaEntrega)}</td>
                  <td>
                    <span
                      className={`status-badge status-${order.estado?.toLowerCase()}`}>
                      {order.estado || "Sin estado"}
                    </span>
                  </td>
                  <td>{formatCurrency(order.costoTotal)}</td>
                  <td>{order.direccionEntrega || "N/A"}</td>
                </tr>
              ))}
            </tbody>
          </table>

          <div className="table-footer">
            <span>Total de pedidos: {filteredOrders.length}</span>
            <div className="actions">
              <button
                className="theme-button"
                disabled={!selectedOrder}
                onClick={() =>
                  alert(`Ver detalles del pedido #${selectedOrder?.id}`)
                }>
                Ver Detalles
              </button>
              <button
                className="theme-button"
                disabled={!selectedOrder}
                onClick={() => setShowDeleteModal(true)}>
                Eliminar
              </button>
            </div>
          </div>
        </>
      )}

      {showDeleteModal && (
        <div className="modal-backdrop">
          <div className="modal-content delete-confirm">
            <p>
              ¿Desea eliminar el pedido #{selectedOrder?.id} del cliente{" "}
              {selectedOrder?.usuarioNombre}?
            </p>
            <div className="modal-actions">
              <button className="theme-button" onClick={handleDelete}>
                Aceptar
              </button>
              <button
                className="theme-button"
                onClick={() => setShowDeleteModal(false)}>
                Cancelar
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default AdminOrders;
