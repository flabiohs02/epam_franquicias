variable "aws_region" {
  description = "Región de AWS para desplegar la infraestructura"
  type        = string
  default     = "us-east-1"
}

variable "environment" {
  description = "Entorno de despliegue (dev, staging, production)"
  type        = string
  default     = "production"
}

variable "project_name" {
  description = "Nombre base del proyecto para identificar los recursos"
  type        = string
  default     = "franquicias"
}

variable "vpc_cidr" {
  description = "Bloque CIDR principal para la VPC"
  type        = string
  default     = "10.0.0.0/16"
}

variable "public_subnet_cidrs" {
  description = "Bloques CIDR para las subredes públicas (Multi-AZ)"
  type        = list(string)
  default     = ["10.0.1.0/24", "10.0.2.0/24"]
}

variable "private_subnet_cidrs" {
  description = "Bloques CIDR para las subredes privadas (Multi-AZ)"
  type        = list(string)
  default     = ["10.0.10.0/24", "10.0.11.0/24"]
}

variable "backend_image" {
  description = "URI de la imagen Docker en Amazon ECR para el backend (Spring Boot)"
  type        = string
  default     = "123456789012.dkr.ecr.us-east-1.amazonaws.com/franquicias-backend:latest"
}

variable "frontend_image" {
  description = "URI de la imagen Docker en Amazon ECR para el frontend (Angular)"
  type        = string
  default     = "123456789012.dkr.ecr.us-east-1.amazonaws.com/franquicias-frontend:latest"
}

variable "backend_cpu" {
  description = "Unidades de CPU asignadas a la tarea de backend en ECS Fargate (256 = 0.25 vCPU, 512 = 0.5 vCPU)"
  type        = number
  default     = 512
}

variable "backend_memory" {
  description = "Memoria RAM en MB asignada a la tarea de backend en ECS Fargate"
  type        = number
  default     = 1024
}

variable "frontend_cpu" {
  description = "Unidades de CPU asignadas a la tarea de frontend en ECS Fargate"
  type        = number
  default     = 256
}

variable "frontend_memory" {
  description = "Memoria RAM en MB asignada a la tarea de frontend en ECS Fargate"
  type        = number
  default     = 512
}

variable "backend_desired_count" {
  description = "Número deseado de réplicas para el servicio backend"
  type        = number
  default     = 2
}

variable "frontend_desired_count" {
  description = "Número deseado de réplicas para el servicio frontend"
  type        = number
  default     = 2
}

variable "mongodb_uri" {
  description = "Cadena de conexión segura hacia MongoDB Atlas o AWS DocumentDB"
  type        = string
  sensitive   = true
  default     = "mongodb://admin:secret@mongodb-cluster.xyz.mongodb.net:27017/franquicias_db?retryWrites=true&w=majority"
}
