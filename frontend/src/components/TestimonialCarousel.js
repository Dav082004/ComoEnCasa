import React, { useState, useEffect } from "react";
import { testimonios } from "../data/testimonios";
import { Link } from "react-router-dom";

const TestimonialCarousel = () => {
  const [activeIndex, setActiveIndex] = useState(0);

  useEffect(() => {
    const interval = setInterval(() => {
      setActiveIndex((prev) => (prev + 1) % testimonios.length);
    }, 2000);
    return () => clearInterval(interval);
  }, []);

  return (
    <section
      className="py-5"
      style={{
        backgroundColor: "#fff9fa",
        backgroundImage: "linear-gradient(to bottom, #ffecf1, #fff9fa 20%)",
      }}>
      <div className="container">
        <h2
          className="text-center mb-5 fw-bold"
          style={{
            color: "#d63384",
            position: "relative",
            display: "inline-block",
            margin: "0 auto",
            padding: "0 20px",
          }}>
          <span
            style={{
              position: "absolute",
              top: "-10px",
              left: "0",
              width: "100%",
              height: "3px",
              background: "linear-gradient(to right, #ff6b9d, #e83e8c)",
              borderRadius: "3px",
            }}></span>
          Lo que dicen nuestros clientes
          <span
            style={{
              position: "absolute",
              bottom: "-10px",
              right: "0",
              width: "100%",
              height: "3px",
              background: "linear-gradient(to left, #ff6b9d, #e83e8c)",
              borderRadius: "3px",
            }}></span>
        </h2>

        <div
          style={{
            minHeight: "200px",
            position: "relative",
            padding: "30px",
            backgroundColor: "white",
            borderRadius: "15px",
            boxShadow: "0 5px 15px rgba(214, 51, 132, 0.1)",
            border: "1px solid #ffecf1",
            borderTop: "8px solid #ff6b9d",
            margin: "0 auto",
            maxWidth: "800px",
          }}>
          {/* Decoración de cupcakes en las esquinas */}
          <div
            style={{
              position: "absolute",
              top: "-15px",
              left: "20px",
              width: "40px",
              height: "40px",
              background: "#ffecf1",
              borderRadius: "50%",
              display: "flex",
              alignItems: "center",
              justifyContent: "center",
              border: "2px solid #ff6b9d",
            }}>
            <span style={{ fontSize: "20px" }}>🧁</span>
          </div>
          <div
            style={{
              position: "absolute",
              top: "-15px",
              right: "20px",
              width: "40px",
              height: "40px",
              background: "#ffecf1",
              borderRadius: "50%",
              display: "flex",
              alignItems: "center",
              justifyContent: "center",
              border: "2px solid #ff6b9d",
            }}>
            <span style={{ fontSize: "20px" }}>🎂</span>
          </div>

          {testimonios.map((testimonio, index) => (
            <div
              key={index}
              style={{
                position: "absolute",
                width: "calc(100% - 60px)",
                top: "30px",
                left: "30px",
                opacity: index === activeIndex ? 1 : 0,
                transition: "opacity 0.8s ease",
                padding: "20px",
              }}>
              <div className="text-center">
                <p
                  className="fs-5 fst-italic mb-3"
                  style={{
                    color: "#5a1a4a",
                    position: "relative",
                    padding: "0 20px",
                  }}>
                  <span
                    style={{
                      position: "absolute",
                      left: "0",
                      top: "0",
                      fontSize: "30px",
                      color: "#ffc0cb",
                      lineHeight: "1",
                    }}>
                    "
                  </span>
                  {testimonio.mensaje}
                  <span
                    style={{
                      position: "absolute",
                      right: "0",
                      bottom: "0",
                      fontSize: "30px",
                      color: "#ffc0cb",
                      lineHeight: "1",
                    }}>
                    "
                  </span>
                </p>
                <p
                  className="fw-bold mt-4"
                  style={{
                    color: "#e83e8c",
                    fontStyle: "italic",
                  }}>
                  {testimonio.autor}
                </p>
              </div>
            </div>
          ))}
        </div>

        <div className="d-flex justify-content-center mt-4">
          {testimonios.map((_, index) => (
            <Link
              key={index}
              to="#"
              onClick={(e) => {
                e.preventDefault();
                setActiveIndex(index);
              }}
              style={{
                display: "block",
                width: "15px",
                height: "15px",
                borderRadius: "50%",
                backgroundColor: index === activeIndex ? "#e83e8c" : "#ffc0cb",
                margin: "0 8px",
                transition: "all 0.6s ease",
                textDecoration: "none",
                transform: index === activeIndex ? "scale(1.2)" : "scale(1)",
                boxShadow:
                  index === activeIndex
                    ? "0 2px 5px rgba(232, 62, 140, 0.3)"
                    : "none",
              }}
            />
          ))}
        </div>
      </div>
    </section>
  );
};

export default TestimonialCarousel;
