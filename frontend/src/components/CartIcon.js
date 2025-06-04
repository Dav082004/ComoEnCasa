// src/components/CartIcon.js
import React from "react";
import { FaShoppingCart } from "react-icons/fa";
import { useProductContext } from "../context/ProductContext";

const CartIcon = () => {
  const { cart } = useProductContext();
  const itemCount = cart.reduce((acc, item) => acc + item.quantity, 0);

  return (
    <div className="position-relative d-inline-block">
      <FaShoppingCart size={24} color="black" />
      {itemCount > 0 && (
        <span
          className="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger"
          style={{ fontSize: "0.6rem", minWidth: "1.2rem" }}
        >
          {itemCount}
        </span>
      )}
    </div>
  );
};

export default CartIcon;
