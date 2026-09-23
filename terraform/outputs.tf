output "alb_dns_name" {
  description = "Nombre DNS público del Application Load Balancer para acceder a la aplicación"
  value       = aws_lb.main.dns_name
}

output "ecs_cluster_name" {
  description = "Nombre del cluster ECS Fargate"
  value       = aws_ecs_cluster.main.name
}

output "backend_service_name" {
  description = "Nombre del servicio ECS Fargate para el Backend"
  value       = aws_ecs_service.backend.name
}

output "frontend_service_name" {
  description = "Nombre del servicio ECS Fargate para el Frontend"
  value       = aws_ecs_service.frontend.name
}

output "vpc_id" {
  description = "ID de la VPC creada"
  value       = aws_vpc.main.id
}
