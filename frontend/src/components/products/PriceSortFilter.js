import React from "react";
import "./styles/PriceSortFilter.css";

const PriceSortFilter = ({ priceSort, onSortChange }) => {
  return (
    <div className="price-sort-filter">
      <select
        value={priceSort || ""}
        onChange={(e) => onSortChange(e.target.value || null)}
        className="sort-select">
        <option value="">Ordenar por</option>
        <option value="asc">Precio: Menor a mayor</option>
        <option value="desc">Precio: Mayor a menor</option>
      </select>
    </div>
  );
};

export default PriceSortFilter;
