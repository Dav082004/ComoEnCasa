import React, { useState } from "react";

const ProductFilters = ({ onFilter }) => {
  const [filters, setFilters] = useState({
    category: "",
    size: "",
    flavor: "",
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFilters((prev) => ({
      ...prev,
      [name]: value,
    }));
    onFilter({ ...filters, [name]: value });
  };

  return (
    <div className="product-filters">
      <div className="filter-group">
        <h4>CATEGORÍAS</h4>
        <select name="category" onChange={handleChange}>
          <option value="">General</option>
          <option value="tortas">Tortas</option>
          <option value="postres">Postres Caseros</option>
          <option value="dulces">Detalles Dulces</option>
        </select>
      </div>

      <div className="filter-group">
        <h4>TAMAÑOS</h4>
        <select name="size" onChange={handleChange}>
          <option value="">Todos</option>
          <option value="individual">Individual</option>
          <option value="pequena">Pequeña</option>
          <option value="mediana">Mediana</option>
          <option value="grande">Grande</option>
        </select>
      </div>

      <div className="filter-group">
        <h4>SABOR</h4>
        <select name="flavor" onChange={handleChange}>
          <option value="">Todos</option>
          <option value="chocolate">Chocolate</option>
          <option value="vainilla">Vainilla</option>
          <option value="mixto">Mixto</option>
        </select>
      </div>
    </div>
  );
};

export default ProductFilters;
