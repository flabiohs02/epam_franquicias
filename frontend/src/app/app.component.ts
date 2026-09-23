import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { FranchiseService } from './core/services/franchise.service';
import { Franchise, Branch, Product, BranchTopProduct } from './core/models/franchise.model';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  private franchiseService = inject(FranchiseService);

  activeTab: 'management' | 'analytics' = 'management';

  franchises: Franchise[] = [];
  selectedFranchise: Franchise | null = null;
  selectedBranch: Branch | null = null;

  // Analítica (Requerimiento 7)
  analyticsFranchiseId: string = '';
  topProducts: BranchTopProduct[] = [];
  loadingAnalytics: boolean = false;

  // Estados de interfaz y feedback
  loading: boolean = false;
  successMessage: string | null = null;
  errorMessage: string | null = null;

  // Modales
  showFranchiseModal: boolean = false;
  newFranchiseName: string = '';

  showBranchModal: boolean = false;
  newBranchName: string = '';

  showProductModal: boolean = false;
  newProductName: string = '';
  newProductStock: number = 0;

  showStockModal: boolean = false;
  stockEditProduct: Product | null = null;
  stockEditBranchId: string = '';
  updatedStockValue: number = 0;

  showEditNameModal: boolean = false;
  editNameType: 'franchise' | 'branch' | 'product' = 'franchise';
  editNameTargetId: string = '';
  editNameSecondaryId: string = '';
  editNameValue: string = '';

  ngOnInit(): void {
    this.loadFranchises();
  }

  loadFranchises(): void {
    this.loading = true;
    this.franchiseService.getAllFranchises().subscribe({
      next: (data) => {
        this.franchises = data;
        if (data.length > 0) {
          if (!this.selectedFranchise) {
            this.selectFranchise(data[0]);
          } else {
            const updated = data.find((f) => f.id === this.selectedFranchise?.id);
            if (updated) {
              this.selectedFranchise = updated;
            } else {
              this.selectFranchise(data[0]);
            }
          }
          if (!this.analyticsFranchiseId) {
            this.analyticsFranchiseId = data[0].id;
            this.loadAnalytics();
          }
        } else {
          this.selectedFranchise = null;
        }
        this.loading = false;
      },
      error: (err) => {
        this.showError('Error al cargar las franquicias', err);
        this.loading = false;
      }
    });
  }

  selectFranchise(franchise: Franchise): void {
    this.selectedFranchise = franchise;
    if (this.activeTab === 'analytics') {
      this.analyticsFranchiseId = franchise.id;
      this.loadAnalytics();
    }
  }

  // 1. Crear Franquicia
  openCreateFranchise(): void {
    this.newFranchiseName = '';
    this.showFranchiseModal = true;
  }

  createFranchise(): void {
    if (!this.newFranchiseName.trim()) {
      this.showError('El nombre de la franquicia es obligatorio');
      return;
    }
    this.loading = true;
    this.franchiseService.createFranchise({ name: this.newFranchiseName.trim() }).subscribe({
      next: (created) => {
        this.showSuccess(`Franquicia "${created.name}" creada exitosamente`);
        this.showFranchiseModal = false;
        this.loadFranchises();
      },
      error: (err) => this.showError('Error al crear franquicia', err)
    });
  }

  // 2. Agregar Sucursal
  openAddBranch(): void {
    if (!this.selectedFranchise) return;
    this.newBranchName = '';
    this.showBranchModal = true;
  }

  addBranch(): void {
    if (!this.selectedFranchise || !this.newBranchName.trim()) {
      this.showError('El nombre de la sucursal es obligatorio');
      return;
    }
    this.loading = true;
    this.franchiseService.addBranch(this.selectedFranchise.id, { name: this.newBranchName.trim() }).subscribe({
      next: (branch) => {
        this.showSuccess(`Sucursal "${branch.name}" agregada exitosamente`);
        this.showBranchModal = false;
        this.loadFranchises();
      },
      error: (err) => this.showError('Error al agregar sucursal', err)
    });
  }

  // 3. Agregar Producto
  openAddProduct(branch: Branch): void {
    this.selectedBranch = branch;
    this.newProductName = '';
    this.newProductStock = 0;
    this.showProductModal = true;
  }

  addProduct(): void {
    if (!this.selectedFranchise || !this.selectedBranch) return;
    if (!this.newProductName.trim()) {
      this.showError('El nombre del producto es obligatorio');
      return;
    }
    if (this.newProductStock < 0) {
      this.showError('El stock no puede ser negativo');
      return;
    }
    this.loading = true;
    this.franchiseService.addProduct(this.selectedFranchise.id, this.selectedBranch.id, {
      name: this.newProductName.trim(),
      stock: this.newProductStock
    }).subscribe({
      next: (product) => {
        this.showSuccess(`Producto "${product.name}" agregado con éxito`);
        this.showProductModal = false;
        this.loadFranchises();
      },
      error: (err) => this.showError('Error al agregar producto', err)
    });
  }

  // 4. Eliminar Producto
  deleteProduct(branchId: string, product: Product): void {
    if (!this.selectedFranchise) return;
    if (!confirm(`¿Estás seguro de eliminar el producto "${product.name}"?`)) return;

    this.loading = true;
    this.franchiseService.deleteProduct(this.selectedFranchise.id, branchId, product.id).subscribe({
      next: () => {
        this.showSuccess(`Producto "${product.name}" eliminado correctamente`);
        this.loadFranchises();
      },
      error: (err) => this.showError('Error al eliminar producto', err)
    });
  }

  // 5. Modificar Stock
  openEditStock(branchId: string, product: Product): void {
    this.stockEditBranchId = branchId;
    this.stockEditProduct = product;
    this.updatedStockValue = product.stock;
    this.showStockModal = true;
  }

  saveStock(): void {
    if (!this.selectedFranchise || !this.stockEditProduct) return;
    if (this.updatedStockValue < 0) {
      this.showError('El stock no puede ser negativo');
      return;
    }
    this.loading = true;
    this.franchiseService.updateStock(
      this.selectedFranchise.id,
      this.stockEditBranchId,
      this.stockEditProduct.id,
      { newStock: this.updatedStockValue }
    ).subscribe({
      next: (updated) => {
        this.showSuccess(`Stock de "${updated.name}" actualizado a ${updated.stock}`);
        this.showStockModal = false;
        this.loadFranchises();
      },
      error: (err) => this.showError('Error al modificar stock', err)
    });
  }

  // 6. Actualizar Nombres
  openEditFranchiseName(franchise: Franchise): void {
    this.editNameType = 'franchise';
    this.editNameTargetId = franchise.id;
    this.editNameValue = franchise.name;
    this.showEditNameModal = true;
  }

  openEditBranchName(branch: Branch): void {
    this.editNameType = 'branch';
    this.editNameTargetId = branch.id;
    this.editNameValue = branch.name;
    this.showEditNameModal = true;
  }

  openEditProductName(branchId: string, product: Product): void {
    this.editNameType = 'product';
    this.editNameTargetId = product.id;
    this.editNameSecondaryId = branchId;
    this.editNameValue = product.name;
    this.showEditNameModal = true;
  }

  saveNameEdit(): void {
    if (!this.editNameValue.trim() || !this.selectedFranchise) return;
    this.loading = true;

    if (this.editNameType === 'franchise') {
      this.franchiseService.updateFranchiseName(this.editNameTargetId, { name: this.editNameValue.trim() }).subscribe({
        next: () => {
          this.showSuccess('Nombre de franquicia actualizado');
          this.showEditNameModal = false;
          this.loadFranchises();
        },
        error: (err) => this.showError('Error al actualizar nombre de franquicia', err)
      });
    } else if (this.editNameType === 'branch') {
      this.franchiseService.updateBranchName(this.selectedFranchise.id, this.editNameTargetId, { name: this.editNameValue.trim() }).subscribe({
        next: () => {
          this.showSuccess('Nombre de sucursal actualizado');
          this.showEditNameModal = false;
          this.loadFranchises();
        },
        error: (err) => this.showError('Error al actualizar nombre de sucursal', err)
      });
    } else if (this.editNameType === 'product') {
      this.franchiseService.updateProductName(this.selectedFranchise.id, this.editNameSecondaryId, this.editNameTargetId, { name: this.editNameValue.trim() }).subscribe({
        next: () => {
          this.showSuccess('Nombre de producto actualizado');
          this.showEditNameModal = false;
          this.loadFranchises();
        },
        error: (err) => this.showError('Error al actualizar nombre de producto', err)
      });
    }
  }

  // 7. Requerimiento 7: Analítica de Mayor Stock
  onAnalyticsFranchiseChange(): void {
    this.loadAnalytics();
  }

  loadAnalytics(): void {
    if (!this.analyticsFranchiseId) {
      this.topProducts = [];
      return;
    }
    this.loadingAnalytics = true;
    this.franchiseService.getTopStockProducts(this.analyticsFranchiseId).subscribe({
      next: (data) => {
        this.topProducts = data;
        this.loadingAnalytics = false;
      },
      error: (err) => {
        this.showError('Error al obtener reporte analítico', err);
        this.loadingAnalytics = false;
      }
    });
  }

  // Helpers
  getTotalProductsCount(franchise: Franchise): number {
    return franchise.branches?.reduce((acc, b) => acc + (b.products?.length || 0), 0) || 0;
  }

  getTotalStockCount(franchise: Franchise): number {
    return franchise.branches?.reduce(
      (acc, b) => acc + (b.products?.reduce((pAcc, p) => pAcc + p.stock, 0) || 0),
      0
    ) || 0;
  }

  getStockBadgeClass(stock: number): string {
    if (stock > 50) return 'badge-success';
    if (stock >= 10) return 'badge-warning';
    return 'badge-danger';
  }

  private showSuccess(msg: string): void {
    this.successMessage = msg;
    this.errorMessage = null;
    this.loading = false;
    setTimeout(() => {
      if (this.successMessage === msg) this.successMessage = null;
    }, 4000);
  }

  private showError(msg: string, err?: any): void {
    let detail = '';
    if (err?.error?.message) {
      detail = ` - ${err.error.message}`;
    } else if (err?.message) {
      detail = ` - ${err.message}`;
    }
    this.errorMessage = `${msg}${detail}`;
    this.successMessage = null;
    this.loading = false;
    setTimeout(() => {
      this.errorMessage = null;
    }, 6000);
  }
}
