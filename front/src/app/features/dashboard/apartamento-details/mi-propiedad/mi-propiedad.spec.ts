import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MiPropiedad } from './mi-propiedad';

describe('MiPropiedad', () => {
  let component: MiPropiedad;
  let fixture: ComponentFixture<MiPropiedad>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MiPropiedad],
    }).compileComponents();

    fixture = TestBed.createComponent(MiPropiedad);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
