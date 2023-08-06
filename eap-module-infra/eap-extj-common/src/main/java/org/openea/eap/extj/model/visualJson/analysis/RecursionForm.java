package org.openea.eap.extj.model.visualJson.analysis;

import lombok.Data;
import org.openea.eap.extj.model.visualJson.FieLdsModel;
import org.openea.eap.extj.model.visualJson.TableModel;

import java.util.List;

@Data
public class RecursionForm {

    private List<FieLdsModel> list;
    private List<TableModel> tableModelList;

    public static RecursionFormBuilder builder() {
        return new RecursionFormBuilder();
    }

    public RecursionForm() {
    }

    public RecursionForm(List<FieLdsModel> list, List<TableModel> tableModelList) {
        this.list = list;
        this.tableModelList = tableModelList;
    }

    public static class RecursionFormBuilder {
        private List<FieLdsModel> list;
        private List<TableModel> tableModelList;

        RecursionFormBuilder() {
        }

        public RecursionFormBuilder list(List<FieLdsModel> list) {
            this.list = list;
            return this;
        }

        public RecursionFormBuilder tableModelList(List<TableModel> tableModelList) {
            this.tableModelList = tableModelList;
            return this;
        }

        public RecursionForm build() {
            return new RecursionForm(this.list, this.tableModelList);
        }

        public String toString() {
            return "RecursionForm.RecursionFormBuilder(list=" + this.list + ", tableModelList=" + this.tableModelList + ")";
        }
    }



}
