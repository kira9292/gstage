import { ContractStatus } from "../enums/trainee.enum";


export interface DocumentsCard {
  title: string;
  description: string;
  icon: string;
  count?: number;
  route: string;
  type: string;
}

export interface Contract {
    id: number;
    reference: string;
    type: string;
    name: string;
    startDate: Date;
    endDate: Date;
    compensation: number;
    status: ContractStatus;
    assignmentSite: string;
    signatureDate?: Date;
    comments?: string;
    url: string;
  }

  export interface AttestationPresence {
    id: number;
    reference: string;
    month: string;
    startDate: Date;
    endDate: Date;
    signatureDate: Date;
    status: boolean;
    comments?: string;
    url: string;
  }
  
  export interface PaginationInfo {
    currentPage: number;
    pageSize: number;
    totalItems: number;
    totalPages: number;
  }


  