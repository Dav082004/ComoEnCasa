import React from "react";


import slide from "../assets/slide.jpg";
import PastelDeChocolate from "../assets/tortas/torta_1.webp";
import PiedeMazana from "../assets/postres/postre_1.webp";

const Home = () => {
  return (
    <div className="container">

      <div className="content">
        <img src={slide} alt="Slide" />
        <h1 className="text-center slogan">Tortas y pasteles que recuerden al hogar</h1>
      </div>

      <section className="container top-categories">
    <div className="container-categories">
      <div className="card-category category-Pasteles">
        <p>Pasteles</p>
      </div>
      <div className="card-category category-Postres">
        <p>Postres</p>
      </div>
      <div className="card-category category-Eventos">
        <p>Eventos</p>
      </div>
    </div>
  </section>

  <section class="container blogs">
				<h1 class="heading-1">Últimos Productos</h1>

				<div class="container-blogs">
					<div class="card-blog">
						<div class="container-img">
						<img src={PastelDeChocolate} alt="PastelDeChocolate" />
							<div class="button-group-blog">
								<span>
									<i class="fa-solid fa-magnifying-glass"></i>
								</span>
								<span>
									<i class="fa-solid fa-link"></i>
								</span>
							</div>
						</div>
						<div class="content-blog">
							<h3>Pastel de Chocolate</h3>
							<span>02 Mayo 2025</span>
							<p>
              Un postre delicioso y esponjoso, hecho con capas de bizcocho de cacao y cubierto con una rica crema o ganache de chocolate. Ideal para los amantes del dulce.
							</p>
						</div>
					</div>
					<div class="card-blog">
						<div class="container-img">
						<img src={PiedeMazana} alt="PiedeManzana" />
							<div class="button-group-blog">
								<span>
									<i class="fa-solid fa-magnifying-glass"></i>
								</span>
								<span>
									<i class="fa-solid fa-link"></i>
								</span>
							</div>
						</div>
						<div class="content-blog">
							<h3>Pie de Manzana</h3>
							<span>25 Abril 2025</span>
							<p>
              Clásico postre con relleno de manzanas caramelizadas y especias, envuelto en una crujiente masa dorada. Perfecto para disfrutar caliente con una bola de helado.
							</p>
						</div>
					</div>
				</div>
			</section>
    </div>
    
    
    
  );
};

export default Home;
