import React from "react";
import "./styles/CategoryFilter.css";

const CategoryFilter = ({ selectedCategory, onSelectCategory }) => {
  const categories = [
    { id: "1", name: "Tortas" },
    { id: "2", name: "Eventos" },
    { id: "3", name: "Postres" },
  ];

  return (
    <div className="category-filter">
      <h3 className="filter-title">Categorías</h3>
      <ul className="category-list">
        <li className="category-item">
          <button
            className={`category-btn ${!selectedCategory ? "active" : ""}`}
            onClick={() => onSelectCategory(null)}>
            Todas las categorías
          </button>
        </li>
        {categories.map((category) => (
          <li key={category.id} className="category-item">
            <button
              className={`category-btn ${
                selectedCategory === category.id ? "active" : ""
              }`}
              onClick={() => onSelectCategory(category.id)}>
              {category.name}
            </button>
          </li>
        ))}
      </ul>
    </div>
  );
};

export default CategoryFilter;
