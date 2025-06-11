import React, { useEffect, useState } from 'react';
import axios from 'axios';
import '../styles/Admin.css';

function AdminOrders() {
  const [orders, setOrders] = useState([]);
  const [filteredOrders, setFilteredOrders] = useState([]);
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedOrder, setSelectedOrder] = useState(null);
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [showAddModal, setShowAddModal] = useState(false);
  const [newOrder, setNewOrder] = useState({
    codigo: '',
    cliente: '',
    fecha: '',
    estado: '',
    total: ''
  });

  useEffect(() => {
    fetchOrders();
  }, []);

  const fetchOrders = async () => {
    try {
      const response = await axios.get('http://localhost:3000/pedidos');
      setOrders(response.data);
      setFilteredOrders(response.data);
    } catch (error) {
      console.error('Error al cargar los pedidos:', error);
    }
  };

  const handleSearch = () => {
    const filtered = orders.filter(order =>
      order.codigo?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      order.cliente?.toLowerCase().includes(searchTerm.toLowerCase())
    );
    setFilteredOrders(filtered);
  };

  const handleDelete = async () => {
    try {
      await axios.delete(`http://localhost:3000/pedidos/${selectedOrder.id}`);
      setShowDeleteModal(false);
      fetchOrders();
    } catch (error) {
      console.error('Error al eliminar el pedido:', error);
    }
  };

  const handleAdd = async (e) => {
    e.preventDefault();
    try {
      await axios.post('http://localhost:3000/pedidos', newOrder);
      setShowAddModal(false);
      setNewOrder({ codigo: '', cliente: '', fecha: '', estado: '', total: '' });
      fetchOrders();
    } catch (error) {
      console.error('Error al agregar el pedido:', error);
      alert('Error al agregar el pedido');
    }
  };

  const handleChange = (e) => {
    setNewOrder({
      ...newOrder,
      [e.target.name]: e.target.value
    });
  };

  return (
    <div className="page-container">
      <h2 className="theme-header">Gestión de Pedidos</h2>

      <div className="admin-header">
        <input
          type="text"
          placeholder="Buscar por código o cliente..."
          value={searchTerm}
          onChange={e => setSearchTerm(e.target.value)}
        />
        <button className="theme-button" onClick={handleSearch}>Buscar</button>
      </div>

      <table className="admin-table">
        <thead>
          <tr>
            <th>Código</th>
            <th>Cliente</th>
            <th>Fecha</th>
            <th>Estado</th>
            <th>Total</th>
          </tr>
        </thead>
        <tbody>
          {filteredOrders.map(order => (
            <tr
              key={order.id}
              onClick={() => setSelectedOrder(order)}
              className={selectedOrder?.id === order.id ? 'selected-row' : ''}
            >
              <td>{order.codigo}</td>
              <td>{order.cliente}</td>
              <td>{order.fecha}</td>
              <td>{order.estado}</td>
              <td>S/ {order.total}</td>
            </tr>
          ))}
        </tbody>
      </table>

      <div className="table-footer">
        <span>Total de pedidos: {filteredOrders.length}</span>
        <div className="actions">
          <button className="theme-button" onClick={() => setShowAddModal(true)}>Agregar</button>
          <button
            className="theme-button"
            disabled={!selectedOrder}
            onClick={() => alert(`Editar pedido ${selectedOrder?.codigo}`)}
          >
            Editar
          </button>
          <button
            className="theme-button"
            disabled={!selectedOrder}
            onClick={() => setShowDeleteModal(true)}
          >
            Borrar
          </button>
        </div>
      </div>

      {showDeleteModal && (
        <div className="modal-backdrop">
          <div className="modal-content delete-confirm">
            <p>¿Desea borrar el pedido seleccionado?</p>
            <div className="modal-actions">
              <button className="theme-button" onClick={handleDelete}>Aceptar</button>
              <button className="theme-button" onClick={() => setShowDeleteModal(false)}>Cancelar</button>
            </div>
          </div>
        </div>
      )}

      {showAddModal && (
        <div className="modal-backdrop">
          <div className="modal-content">
            <h3>Agregar Pedido</h3>
            <form className="product-form" onSubmit={handleAdd}>
              <input
                type="text"
                name="codigo"
                placeholder="Código"
                value={newOrder.codigo}
                onChange={handleChange}
                required
              />
              <input
                type="text"
                name="cliente"
                placeholder="Cliente"
                value={newOrder.cliente}
                onChange={handleChange}
                required
              />
              <input
                type="date"
                name="fecha"
                placeholder="Fecha"
                value={newOrder.fecha}
                onChange={handleChange}
                required
              />
              <input
                type="text"
                name="estado"
                placeholder="Estado"
                value={newOrder.estado}
                onChange={handleChange}
                required
              />
              <input
                type="number"
                name="total"
                placeholder="Total"
                value={newOrder.total}
                onChange={handleChange}
                required
              />

              <div className="modal-actions">
                <button className="theme-button" type="submit">Guardar</button>
                <button className="theme-button" type="button" onClick={() => setShowAddModal(false)}>Cancelar</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}

export default AdminOrders;
