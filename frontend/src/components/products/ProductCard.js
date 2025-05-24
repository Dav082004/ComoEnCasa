import React from "react";
import { Link } from "react-router-dom";

const ProductCard = ({ product }) => {
  return (
    <div className="product-card">
      <Link to={`/products/${product.id}`}>
        <img src={product.imageUrl} alt={product.name} />
        <h3>{product.name}</h3>
        <div className="price">
          {product.discountedPrice ? (
            <>
              <span className="original-price">S/. {product.price}</span>
              <span className="current-price">
                S/. {product.discountedPrice}
              </span>
            </>
          ) : (
            <span>S/. {product.price}</span>
          )}
        </div>
        {product.includes && <p className="includes">{product.includes}</p>}
      </Link>
    </div>
  );
};

export default ProductCard;
