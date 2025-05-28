import React, { useState } from "react";
import { useCart } from "../context/CartContext";
import {
  Container,
  Row,
  Col,
  Form,
  Button,
  Card,
  ListGroup,
} from "react-bootstrap";
import "../styles/Checkout.css";

const Checkout = () => {
  const { cart } = useCart();
  const [datos, setDatos] = useState({
    departamento: "",
    distrito: "",
    direccion: "",
    referencia: "",
    numero: "",
    tarjeta: "",
    titular: "",
    vencimiento: "",
    cvv: "",
    guardar: false,
    giftcard: "",
  });

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setDatos((prev) => ({
      ...prev,
      [name]: type === "checkbox" ? checked : value,
    }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    alert("Orden finalizada correctamente.");
  };

  const productos = Object.values(cart);
  const subtotal = productos.reduce((acc, p) => acc + p.precio * p.quantity, 0);
  const igv = subtotal * 0.18;
  const total = subtotal + igv;

  return (
    <Container className="my-5 checkout-container">
      <Row>
        <Col lg={8} className="mb-4">
          <Card className="p-4">
            <h2 className="text-pink mb-4">Detalles de Envío</h2>
            <Form onSubmit={handleSubmit}>
              <Row className="mb-3">
                <Col md={6}>
                  <Form.Group controlId="departamento">
                    <Form.Select
                      name="departamento"
                      value={datos.departamento}
                      onChange={handleChange}
                      required>
                      <option value="">Seleccionar Departamento</option>
                      <option value="Lima">Lima</option>
                      <option value="Arequipa">Arequipa</option>
                    </Form.Select>
                  </Form.Group>
                </Col>
                <Col md={6}>
                  <Form.Group controlId="distrito">
                    <Form.Select
                      name="distrito"
                      value={datos.distrito}
                      onChange={handleChange}
                      required>
                      <option value="">Seleccionar Distrito</option>
                      <option value="Miraflores">Miraflores</option>
                      <option value="Surco">Surco</option>
                    </Form.Select>
                  </Form.Group>
                </Col>
              </Row>

              <Row className="mb-3">
                <Col md={8}>
                  <Form.Group controlId="direccion">
                    <Form.Control
                      type="text"
                      name="direccion"
                      placeholder="Ingrese su Dirección"
                      value={datos.direccion}
                      onChange={handleChange}
                      required
                    />
                  </Form.Group>
                </Col>
                <Col md={4}>
                  <Form.Group controlId="numero">
                    <Form.Control
                      type="text"
                      name="numero"
                      placeholder="Número/Dpto/Bloque"
                      value={datos.numero}
                      onChange={handleChange}
                    />
                  </Form.Group>
                </Col>
              </Row>

              <Form.Group className="mb-4" controlId="referencia">
                <Form.Control
                  type="text"
                  name="referencia"
                  placeholder="Referencia (Opcional)"
                  value={datos.referencia}
                  onChange={handleChange}
                />
              </Form.Group>

              <h2 className="text-pink mb-4">Métodos de Pago</h2>

              <Form.Group className="mb-3" controlId="tarjeta">
                <Form.Control
                  type="text"
                  name="tarjeta"
                  placeholder="Número de tarjeta"
                  value={datos.tarjeta}
                  onChange={handleChange}
                  required
                />
              </Form.Group>

              <Row className="mb-3">
                <Col md={6}>
                  <Form.Group controlId="titular">
                    <Form.Control
                      type="text"
                      name="titular"
                      placeholder="Titular de la tarjeta"
                      value={datos.titular}
                      onChange={handleChange}
                      required
                    />
                  </Form.Group>
                </Col>
                <Col md={3}>
                  <Form.Group controlId="vencimiento">
                    <Form.Control
                      type="text"
                      name="vencimiento"
                      placeholder="MM/YY"
                      value={datos.vencimiento}
                      onChange={handleChange}
                      required
                    />
                  </Form.Group>
                </Col>
                <Col md={3}>
                  <Form.Group controlId="cvv">
                    <Form.Control
                      type="text"
                      name="cvv"
                      placeholder="CVV"
                      value={datos.cvv}
                      onChange={handleChange}
                      required
                    />
                  </Form.Group>
                </Col>
              </Row>

              <Form.Group className="mb-4" controlId="guardar">
                <Form.Check
                  type="checkbox"
                  name="guardar"
                  label="Guardar mi información para una compra más rápida"
                  checked={datos.guardar}
                  onChange={handleChange}
                />
              </Form.Group>

              <Button variant="primary" type="submit" className="w-100 py-2">
                Finalizar Orden
              </Button>
            </Form>
          </Card>
        </Col>

        <Col lg={4}>
          <Card className="p-4 checkout-summary">
            <h3 className="text-pink mb-4">Resumen del Pedido</h3>
            <ListGroup variant="flush">
              {productos.map((prod) => (
                <ListGroup.Item
                  key={prod.id}
                  className="d-flex justify-content-between">
                  <span>
                    {prod.nombre} x {prod.quantity}
                  </span>
                  <span>S/. {(prod.precio * prod.quantity).toFixed(2)}</span>
                </ListGroup.Item>
              ))}
            </ListGroup>

            <ListGroup variant="flush" className="mt-3">
              <ListGroup.Item className="d-flex justify-content-between">
                <span>Subtotal:</span>
                <span>S/. {subtotal.toFixed(2)}</span>
              </ListGroup.Item>
              <ListGroup.Item className="d-flex justify-content-between">
                <span>IGV (18%):</span>
                <span>S/. {igv.toFixed(2)}</span>
              </ListGroup.Item>
              <ListGroup.Item className="d-flex justify-content-between fw-bold fs-5">
                <span>Total:</span>
                <span>S/. {total.toFixed(2)}</span>
              </ListGroup.Item>
            </ListGroup>
          </Card>
        </Col>
      </Row>
    </Container>
  );
};

export default Checkout;
