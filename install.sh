#!/bin/bash

# =============================================================================
# COMO EN CASA - SCRIPT DE INSTALACIÓN PARA PORTAFOLIO
# =============================================================================

echo "🍰 Iniciando instalación de Como en Casa..."
echo "=================================================="

# Verificar requisitos
check_requirements() {
    echo "🔍 Verificando requisitos del sistema..."
    
    # Verificar Java
    if ! command -v java &> /dev/null; then
        echo "❌ Java no está instalado. Por favor instala Java 21 o superior."
        exit 1
    fi
    
    # Verificar Node.js
    if ! command -v node &> /dev/null; then
        echo "❌ Node.js no está instalado. Por favor instala Node.js 18 o superior."
        exit 1
    fi
    
    # Verificar Docker (opcional)
    if command -v docker &> /dev/null; then
        echo "✅ Docker encontrado - instalación con contenedores disponible"
        DOCKER_AVAILABLE=true
    else
        echo "⚠️  Docker no encontrado - instalación manual disponible"
        DOCKER_AVAILABLE=false
    fi
    
    echo "✅ Requisitos básicos cumplidos"
}

# Instalación con Docker
install_with_docker() {
    echo "🐳 Instalando con Docker..."
    
    # Crear archivo .env si no existe
    if [ ! -f .env ]; then
        cat > .env << EOF
# Configuración de Base de Datos
DB_USERNAME=root
DB_PASSWORD=root

# Configuración de Email (opcional)
EMAIL_USERNAME=
EMAIL_PASSWORD=

# Configuración de PayPal (opcional)
PAYPAL_CLIENT_ID=
PAYPAL_CLIENT_SECRET=
PAYPAL_ENVIRONMENT=sandbox

# URL del Frontend
FRONTEND_URL=http://localhost:3000
EOF
        echo "📝 Archivo .env creado con configuración por defecto"
    fi
    
    # Construir e iniciar contenedores
    echo "🔨 Construyendo contenedores..."
    docker-compose build --no-cache
    
    echo "🚀 Iniciando servicios..."
    docker-compose up -d
    
    # Esperar a que los servicios estén listos
    echo "⏳ Esperando a que los servicios estén listos..."
    sleep 30
    
    # Verificar estado de los servicios
    echo "🔍 Verificando estado de los servicios..."
    docker-compose ps
    
    echo ""
    echo "🎉 ¡Instalación completada!"
    echo "📱 Frontend: http://localhost:3000"
    echo "🔧 Backend API: http://localhost:8080"
    echo "💾 Base de datos: localhost:3306"
    echo ""
    echo "Para detener los servicios: docker-compose down"
}

# Instalación manual
install_manually() {
    echo "🔧 Instalación manual..."
    
    # Backend
    echo "🍃 Configurando backend..."
    cd backend
    
    # Compilar backend
    if command -v ./mvnw &> /dev/null; then
        ./mvnw clean install -DskipTests
    elif command -v mvn &> /dev/null; then
        mvn clean install -DskipTests
    else
        echo "❌ Maven no encontrado. Por favor instala Maven."
        exit 1
    fi
    
    cd ..
    
    # Frontend
    echo "⚛️ Configurando frontend..."
    cd frontend
    
    # Instalar dependencias
    npm install
    
    cd ..
    
    echo ""
    echo "🎉 ¡Instalación completada!"
    echo ""
    echo "Para iniciar la aplicación:"
    echo "1. Asegúrate de que MySQL esté ejecutándose"
    echo "2. Crea la base de datos 'comoencasa_db'"
    echo "3. Ejecuta el backend: cd backend && ./mvnw spring-boot:run"
    echo "4. Ejecuta el frontend: cd frontend && npm start"
    echo ""
    echo "📱 Frontend: http://localhost:3000"
    echo "🔧 Backend API: http://localhost:8080"
}

# Función principal
main() {
    echo "🍰 ¡Bienvenido a Como en Casa!"
    echo "Sistema de gestión de pedidos para panadería artesanal"
    echo ""
    
    check_requirements
    
    echo ""
    echo "Selecciona el método de instalación:"
    echo "1) Docker (recomendado)"
    echo "2) Manual"
    echo ""
    
    if [ "$DOCKER_AVAILABLE" = true ]; then
        read -p "Ingresa tu opción (1 o 2): " choice
    else
        echo "Docker no está disponible, procediendo con instalación manual..."
        choice=2
    fi
    
    case $choice in
        1)
            install_with_docker
            ;;
        2)
            install_manually
            ;;
        *)
            echo "❌ Opción inválida"
            exit 1
            ;;
    esac
}

# Ejecutar función principal
main "$@"
