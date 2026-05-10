import { Component, Input, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ApartamentoService } from '../../../core/services/apartamento/apartamento.service';

@Component({
  selector: 'app-gestor-documentos',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './gestor-documentos.html'
})
export class GestorDocumentos implements OnInit {
  @Input({ required: true }) apartamentoId!: number;

  private apartamentoService = inject(ApartamentoService);
  private cdr = inject(ChangeDetectorRef);

  documentos: any[] = [];
  cargando = true;
  subiendo = false;
  error = '';

  ngOnInit() {
    this.cargarDocumentos();
  }

  cargarDocumentos() {
    this.cargando = true;
    this.apartamentoService.obtenerDocumentos(this.apartamentoId).subscribe({
      next: (docs) => {
        this.documentos = docs;
        this.cargando = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error al cargar documentos:', err);
        this.error = 'No se pudieron cargar los documentos.';
        this.cargando = false;
        this.cdr.detectChanges();
      }
    });
  }

  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      const archivo = input.files[0];
      this.subirArchivo(archivo);
      this.cdr.detectChanges();
    }
  }

  subirArchivo(archivo: File) {
    this.subiendo = true;
    this.error = '';

    this.apartamentoService.subirDocumento(this.apartamentoId, archivo).subscribe({
      next: () => {
        this.subiendo = false;
        this.cargarDocumentos();
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error al subir:', err);
        this.error = 'Error al subir el archivo.';
        this.subiendo = false;
        this.cdr.detectChanges();
      }
    });
  }

  descargar(doc: any) {
    this.apartamentoService.descargarDocumento(doc.rutaArchivo).subscribe({
      next: (blob) => {
        // Magia para forzar la descarga en el navegador
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = doc.nombreOriginal || 'documento'; // El nombre que le pone el navegador
        document.body.appendChild(a);
        a.click();
        window.URL.revokeObjectURL(url);
        a.remove();
      },
      error: (err) => alert('Error al descargar el archivo.')
    });
  }

  borrar(docId: number) {
    if (confirm('¿Estás seguro de que quieres borrar este documento?')) {
      this.apartamentoService.borrarDocumento(this.apartamentoId, docId).subscribe({
        next: () => this.cargarDocumentos(), // Refrescamos la tabla
        error: (err) => alert('Error al borrar el documento.')
      });
    }
  }
}
