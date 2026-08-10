import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EmprestimoList } from './emprestimo-list';

describe('EmprestimoList', () => {
  let component: EmprestimoList;
  let fixture: ComponentFixture<EmprestimoList>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EmprestimoList],
    }).compileComponents();

    fixture = TestBed.createComponent(EmprestimoList);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
