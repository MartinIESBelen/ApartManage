import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { UsuarioService } from '../../../../core/services/usuario/usuario.service';
import { UsuarioUpdate } from '../../../../core/models/usuario.model';

@Component({
  selector: 'app-editar-perfil',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './editar-perfil.html'
})
export class EditarPerfil implements OnInit {
  private fb = inject(FormBuilder);
  private usuarioService = inject(UsuarioService);
  private router = inject(Router);
  private cdr = inject(ChangeDetectorRef);

  perfilForm!: FormGroup;
  cargando = true;
  guardando = false;
  error = '';

  ngOnInit() {
    this.inicializarFormulario();
    this.cargarDatosActuales();
  }

  inicializarFormulario() {
    this.perfilForm = this.fb.group({
      nombre: ['', [Validators.required, Validators.maxLength(50)]],
      apellidos: ['', [Validators.required, Validators.maxLength(100)]],
      telefono: ['', [Validators.maxLength(20)]],
      dniPasaporte: ['', [Validators.maxLength(50)]],
      fechaNacimiento: ['']
    });
  }

  cargarDatosActuales() {
    this.usuarioService.obtenerMiPerfil().subscribe({
      next: (datos) => {
        this.perfilForm.patchValue({
          nombre: datos.nombre,
          apellidos: datos.apellidos,
          telefono: datos.telefono,
          dniPasaporte: datos.dniPasaporte,
          fechaNacimiento: datos.fechaNacimiento
        });
        this.cargando = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error al cargar perfil:', err);
        this.error = 'No se pudieron cargar tus datos actuales.';
        this.cargando = false;
        this.cdr.detectChanges();
      }
    });
  }

  guardarCambios() {
    if (this.perfilForm.invalid) {
      this.perfilForm.markAllAsTouched();
      return;
    }

    this.guardando = true;
    this.error = '';

    const datosActualizados: UsuarioUpdate = this.perfilForm.value;

    this.usuarioService.actualizarMiPerfil(datosActualizados).subscribe({
      next: () => {
        this.guardando = false;
        this.router.navigate(['/perfil']);
      },
      error: (err) => {
        console.error('Error al actualizar:', err);
        this.error = 'Ocurrió un error al guardar los cambios.';
        this.guardando = false;
        this.cdr.detectChanges();
      }
    });
  }
}
