import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GestorDocumentos } from './gestor-documentos';

describe('GestorDocumentos', () => {
  let component: GestorDocumentos;
  let fixture: ComponentFixture<GestorDocumentos>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GestorDocumentos],
    }).compileComponents();

    fixture = TestBed.createComponent(GestorDocumentos);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
