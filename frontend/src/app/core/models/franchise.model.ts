export interface Product {
  id: string;
  name: string;
  stock: number;
}

export interface Branch {
  id: string;
  name: string;
  products: Product[];
}

export interface Franchise {
  id: string;
  name: string;
  branches: Branch[];
}

export interface BranchTopProduct {
  branchId: string;
  branchName: string;
  topProduct: Product | null;
}

export interface CreateFranchiseRequest {
  name: string;
}

export interface AddBranchRequest {
  name: string;
}

export interface AddProductRequest {
  name: string;
  stock: number;
}

export interface UpdateStockRequest {
  newStock: number;
}

export interface UpdateNameRequest {
  name: string;
}
