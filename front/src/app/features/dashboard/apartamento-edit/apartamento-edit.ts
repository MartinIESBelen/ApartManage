import { Component, inject, OnInit, ChangeDetectorRef } from '@angular/core'; // <-- 1. IMPORTAMOS ChangeDetectorRef
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { ApartamentoService } from '../../../core/services/apartamento.service';

@Component({
  selector: 'app-apartamento-edit',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './apartamento-edit.html'
})
export class ApartamentoEdit implements OnInit {

  private fb = inject(FormBuilder);
  private apartamentoService = inject(ApartamentoService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private cd = inject(ChangeDetectorRef); // <-- 2. LO INYECTAMOS AQUÍ

  apartamentoId!: number;
  cargando = true;
  guardando = false;
  mensajeError = '';

  formApartamento: FormGroup = this.fb.group({
    nombre: ['', [Validators.required, Validators.minLength(3)]],
    direccion: ['', Validators.required],
    ciudad: ['', Validators.required],
    descripcion: ['']
  });

  ngOnInit() {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.apartamentoId = Number(idParam);
      this.cargarDatosApartamento();
    }
  }

  cargarDatosApartamento() {
    this.apartamentoService.getApartamentoById(this.apartamentoId).subscribe({
      next: (apto) => {
        this.formApartamento.patchValue({
          nombre: apto.nombreInterno,
          direccion: apto.direccion,
          ciudad: apto.ciudad,
          descripcion: apto.descripcion
        });
        this.cargando = false;

        this.cd.detectChanges(); // <-- 3. ¡DESPERTAMOS A ANGULAR!
      },
      error: (err) => {
        this.mensajeError = 'Error al cargar los datos del apartamento.';
        this.cargando = false;

        this.cd.detectChanges(); // <-- Y también en caso de error
      }
    });
  }

  onSubmit() {
    if (this.formApartamento.invalid) {
      this.formApartamento.markAllAsTouched();
      return;
    }

    this.guardando = true;
    this.cd.detectChanges();

    this.apartamentoService.actualizarApartamento(this.apartamentoId, this.formApartamento.value).subscribe({
      next: () => {
        this.guardando = false;
        this.router.navigate(['/apartamento', this.apartamentoId]);
      },
      error: (err) => {
        this.mensajeError = 'Error al guardar los cambios.';
        this.guardando = false;
        this.cd.detectChanges();
      }
    });
  }
}
