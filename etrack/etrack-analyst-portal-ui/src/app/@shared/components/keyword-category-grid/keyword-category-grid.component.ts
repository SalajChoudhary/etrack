import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';

@Component({
  selector: 'keyword-category-grid',
  templateUrl: './keyword-category-grid.component.html',
  styleUrls: ['./keyword-category-grid.component.scss']
})
export class KeywordCategoryGridComponent implements OnInit {
  @Input() keywordItems: any[] = [];
  @Input() checkBoxKey: string = '';
  @Input() rounded: boolean = false;
  @Output() changedTimes: EventEmitter<any> = new EventEmitter();
  constructor() { }

  ngOnInit(): void {
  }

  emitChanges(ev: any) {
    this.changedTimes.emit(ev);
  }

}
