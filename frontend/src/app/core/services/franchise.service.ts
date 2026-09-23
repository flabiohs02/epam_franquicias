import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  Franchise,
  Branch,
  Product,
  BranchTopProduct,
  CreateFranchiseRequest,
  AddBranchRequest,
  AddProductRequest,
  UpdateStockRequest,
  UpdateNameRequest
} from '../models/franchise.model';

@Injectable({
  providedIn: 'root'
})
export class FranchiseService {
  private http = inject(HttpClient);

  private getBaseUrl(): string {
    if (typeof window !== 'undefined') {
      const hostname = window.location.hostname;
      // Si corre a través del proxy Nginx en Docker (puerto 4200 o 80)
      if (window.location.port === '4200' || window.location.port === '80' || !window.location.port) {
        return '/api/v1/franchises';
      }
      return `http://${hostname}:8781/api/v1/franchises`;
    }
    return 'http://138.199.212.52:8781/api/v1/franchises';
  }

  private get baseUrl(): string {
    return this.getBaseUrl();
  }

  // Franquicias
  getAllFranchises(): Observable<Franchise[]> {
    return this.http.get<Franchise[]>(this.baseUrl);
  }

  getFranchiseById(id: string): Observable<Franchise> {
    return this.http.get<Franchise>(`${this.baseUrl}/${id}`);
  }

  createFranchise(request: CreateFranchiseRequest): Observable<Franchise> {
    return this.http.post<Franchise>(this.baseUrl, request);
  }

  updateFranchiseName(franchiseId: string, request: UpdateNameRequest): Observable<Franchise> {
    return this.http.patch<Franchise>(`${this.baseUrl}/${franchiseId}/name`, request);
  }

  // Sucursales
  addBranch(franchiseId: string, request: AddBranchRequest): Observable<Branch> {
    return this.http.post<Branch>(`${this.baseUrl}/${franchiseId}/branches`, request);
  }

  updateBranchName(franchiseId: string, branchId: string, request: UpdateNameRequest): Observable<Branch> {
    return this.http.patch<Branch>(`${this.baseUrl}/${franchiseId}/branches/${branchId}/name`, request);
  }

  // Productos
  addProduct(franchiseId: string, branchId: string, request: AddProductRequest): Observable<Product> {
    return this.http.post<Product>(`${this.baseUrl}/${franchiseId}/branches/${branchId}/products`, request);
  }

  deleteProduct(franchiseId: string, branchId: string, productId: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${franchiseId}/branches/${branchId}/products/${productId}`);
  }

  updateStock(franchiseId: string, branchId: string, productId: string, request: UpdateStockRequest): Observable<Product> {
    return this.http.patch<Product>(`${this.baseUrl}/${franchiseId}/branches/${branchId}/products/${productId}/stock`, request);
  }

  updateProductName(franchiseId: string, branchId: string, productId: string, request: UpdateNameRequest): Observable<Product> {
    return this.http.patch<Product>(`${this.baseUrl}/${franchiseId}/branches/${branchId}/products/${productId}/name`, request);
  }

  // Requerimiento 7: Mayor stock por sucursal
  getTopStockProducts(franchiseId: string): Observable<BranchTopProduct[]> {
    return this.http.get<BranchTopProduct[]>(`${this.baseUrl}/${franchiseId}/top-stock-products`);
  }
}
