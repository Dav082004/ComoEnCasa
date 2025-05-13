
// Imágenes pequeñas
import producto1 from "../assets/productos/arroz_con_leche.png";
import producto2 from "../assets/productos/leche_asada.png";
import producto3 from "../assets/productos/mazamorra.png";
import producto4 from "../assets/productos/pastel_de_choclo.png";
import producto5 from "../assets/productos/pastel_de_fresa.png";
import producto6 from "../assets/productos/pastel_de_queso.png";
import producto7 from "../assets/productos/pastel_de_tres_leches.png";
import producto8 from "../assets/productos/picarones.png";
import producto9 from "../assets/productos/suspiro_a_la_limeña.png";
import producto10 from "../assets/productos/torta_de_manzana.png";
import producto11 from "../assets/productos/torta_helada.png";
import producto12 from "../assets/productos/turron.png";
import producto13 from "../assets/productos/flan.png";
import TortadeChocolate from "../assets/tortas/torta_1.webp";
import TortadeVainilla from "../assets/tortas/torta_2.webp";
import TortadeLucuma from "../assets/tortas/torta_3.webp";
import TortadeFresa from "../assets/tortas/torta_4.webp";
import Cumpleaños from "../assets/eventos/eventos_1.webp";
import Graduacion from "../assets/eventos/eventos_3.webp";
import SanValentin from "../assets/eventos/eventos_4.webp";
import postre1 from "../assets/postres/postre_1.webp";
import postre2 from "../assets/postres/postre_2.webp";
import postre3 from "../assets/postres/postre_3.webp";
import postre4 from "../assets/postres/postre_4.webp";

// Imágenes grandes
import grande1 from "../assets/productos_grandes/arroz_con_leche_grande.png";
import grande2 from "../assets/productos_grandes/leche_asada_grande.png";
import grande3 from "../assets/productos_grandes/mazamorra_grande.png";
import grande4 from "../assets/productos_grandes/pastel_de_choclo_grande.png";
import grande5 from "../assets/productos_grandes/pastel_de_fresa_grande.png";
import grande6 from "../assets/productos_grandes/pastel_de_queso_grande.png";
import grande7 from "../assets/productos_grandes/pastel_de_tres_leches_grande.png";
import grande8 from "../assets/productos_grandes/picarones_grande.png";
import grande9 from "../assets/productos_grandes/suspiro_a_la_limeña_grande.png";
import grande10 from "../assets/productos_grandes/torta_de_manzana_grande.png";
import grande11 from "../assets/productos_grandes/torta_helada_grande.png";
import grande12 from "../assets/productos_grandes/turron_grande.png";
import grande13 from "../assets/productos_grandes/torta_de_chocolate_grande.png";
import grande14 from "../assets/productos_grandes/torta_de_vainilla_grande.png";
import grande15 from "../assets/productos_grandes/torta_de_lucuma_grande.png";
import grande16 from "../assets/productos_grandes/torta_de_fresa_grande.png";
import grande17 from "../assets/productos_grandes/torta_de_cumpleaños_grande.png";
import grande18 from "../assets/productos_grandes/torta_de_graduacion_grande.png";
import grande19 from "../assets/productos_grandes/torta_san_valentin_grande.png";
import grande20 from "../assets/productos_grandes/pie_de_manzana_grande.png";
import grande21 from "../assets/productos_grandes/mousse_de_lucuma_grande.png";
import grande22 from "../assets/productos_grandes/mousse_de_maracuya_grande.png";
import grande23 from "../assets/productos_grandes/delirium_grande.png";
import grande24 from "../assets/productos_grandes/flan_grande.png";

// Datos de los productos
export const productosData = [
  { id: 1, img: producto1, imgGrande: grande1, nombre: "Arroz con Leche", precio: 8, descripcion: "Un clásico reconfortante de la cocina peruana, preparado con arroz suave, leche fresca, canela y un toque de vainilla. Perfecto para disfrutar frío o caliente" },
  { id: 2, img: producto2, imgGrande: grande2, nombre: "Leche Asada", precio: 5, descripcion: "Postre tradicional horneado que combina leche, huevos y azúcar para crear una textura suave y caramelizada que se deshace en la boca" },
  { id: 3, img: producto3, imgGrande: grande3, nombre: "Mazamorra Morada", precio: 10, descripcion: "Elaborada con maíz morado, frutas secas y especias peruanas. Un postre colorido, nutritivo y lleno de sabor que evoca la tradición limeña" },
  { id: 4, img: producto4, imgGrande: grande4, nombre: "Pastel de Choclo", precio: 35, descripcion: "Hecho con maíz tierno y queso fundido, este pastel combina lo dulce y salado en cada mordida. Ideal como entrada o acompañamiento" },
  { id: 5, img: producto5, imgGrande: grande5, nombre: "Pastel de Fresa", precio: 40, descripcion: "Bizcocho suave relleno con crema batida fresca y cubierto con jugosas fresas naturales. Una delicia dulce y refrescante" },
  { id: 6, img: producto6, imgGrande: grande6, nombre: "Pastel de Queso", precio: 38, descripcion: "Cremoso, sedoso y con una base crujiente de galleta. Nuestro cheesecake es un balance perfecto entre dulzura y textura" },
  { id: 7, img: producto7, imgGrande: grande7, nombre: "Pastel de Tres Leches", precio: 34, descripcion: "Bizcocho esponjoso empapado en una mezcla de tres leches, coronado con crema batida. Un postre húmedo y celestial" },
  { id: 8, img: producto8, imgGrande: grande8, nombre: "Picarones", precio: 10, descripcion: "Aros fritos de masa de camote y zapallo, bañados con miel de chancaca. Crujientes por fuera, suaves por dentro" },
  { id: 9, img: producto9, imgGrande: grande9, nombre: "Suspiro a La Limeña", precio: 5, descripcion: "Dulce emblema limeño con una base de manjar blanco y un merengue ligero de oporto. Tan delicado como delicioso" },
  { id: 10, img: producto10, imgGrande: grande10, nombre: "Torta de Manzana", precio: 25, descripcion: "Tarta casera con capas de manzana caramelizada y una base esponjosa. Sabor natural y acogedor en cada bocado" },
  { id: 11, img: producto11, imgGrande: grande11, nombre: "Torta Helada", precio: 36, descripcion: "Refrescante pastel frío con gelatina, crema y frutas. El favorito para celebraciones en verano" },
  { id: 12, img: producto12, imgGrande: grande12, nombre: "Turrón", precio: 27, descripcion: "Masa dulce crocante cubierta con miel de frutas y decorado con grajeas de colores. Un ícono de la tradición peruana" },
  { id: 13, img: TortadeChocolate, imgGrande: grande13, nombre: "Torta de Chocolate", precio: 58, descripcion: "Rica y húmeda, con múltiples capas de bizcocho de cacao intenso y ganache cremosa. Un verdadero placer para amantes del chocolate" },
  { id: 14, img: TortadeVainilla, imgGrande: grande14, nombre: "Torta de Vainilla", precio: 45, descripcion: "Clásica y delicada, con esponjoso bizcocho de vainilla y relleno de crema suave. Ideal para cualquier celebración" },
  { id: 15, img: TortadeLucuma, imgGrande: grande15, nombre: "Torta de Lúcuma", precio: 60, descripcion: "Sabores peruanos en su máxima expresión. Bizcocho suave con mousse de lúcuma y cobertura de chocolate" },
  { id: 16, img: TortadeFresa, imgGrande: grande16, nombre: "Torta de Fresa", precio: 42, descripcion: "Ligera, dulce y con un toque ácido natural de las fresas frescas. Perfecta para los que aman los sabores frutales" },
  { id: 17, img: Cumpleaños, imgGrande: grande17, nombre: "Cumpleaños", precio: 62, descripcion: "Personalizada y colorida, perfecta para celebrar con alegría. Rellena de amor y sabor, como toda buena fiesta" },
  { id: 18, img: Graduacion, imgGrande: grande18, nombre: "Graduación", precio: 45, descripcion: "Diseñada para celebrar logros. Elegante, sabrosa y decorada con detalles que marcan un hito inolvidable" },
  { id: 19, img: SanValentin, imgGrande: grande19, nombre: "San Valentín", precio: 60, descripcion: "Romántica y delicada, con detalles en forma de corazón y sabores que enamoran. Comparte con alguien especial" },
  { id: 20, img: postre1, imgGrande: grande20, nombre: "Pye de Manzana", precio: 50, descripcion: "Relleno tibio de manzanas sazonadas con canela, cubierto con una costra dorada y crujiente. Un clásico reconfortante" },
  { id: 21, img: postre2, imgGrande: grande21, nombre: "Mousse de Lúcuma", precio: 43, descripcion: "Textura cremosa y sabor exótico. El mousse de lúcuma es ligero, elegante y totalmente peruano" },
  { id: 22, img: postre3, imgGrande: grande22, nombre: "Mousse de Maracuyá", precio: 50, descripcion: "Dulce y ácido en perfecta armonía. Refrescante mousse con fruta natural de maracuyá, ideal como postre ligero" },
  { id: 23, img: postre4, imgGrande: grande23, nombre: "Delirium", precio: 45, descripcion: "Una creación intensa de chocolate y crema que hace honor a su nombre. Rico, indulgente y absolutamente adictivo" },
  { id: 24, img: producto13, imgGrande: grande24, nombre: "Flan de Caramelo", precio: 6, descripcion: "Postre suave y cremoso, bañado en un caramelo dorado que se derrite en la boca. Su textura sedosa y su dulzura equilibrada lo convierten en un favorito universal" },
];