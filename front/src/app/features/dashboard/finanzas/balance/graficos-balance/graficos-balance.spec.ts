import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GraficosBalance } from './graficos-balance';

describe('GraficosBalance', () => {
  let component: GraficosBalance;
  let fixture: ComponentFixture<GraficosBalance>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GraficosBalance],
    }).compileComponents();

    fixture = TestBed.createComponent(GraficosBalance);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
