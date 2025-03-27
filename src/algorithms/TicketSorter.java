package algorithms;

import model.Ticket;
import java.util.List;

/**
 * Sorts tickets using Merge Sort algorithm.
 * Demonstrates recursion and O(n log n) complexity.
 */
public class TicketSorter {

    public static void mergeSort(List<Ticket> list) {
        if (list == null || list.size() <= 1) return;
        mergeSortRecursive(list, 0, list.size() - 1);
    }

    private static void mergeSortRecursive(List<Ticket> list, int left, int right) {
        if (left < right) {
            int mid = (left + right) / 2;
            mergeSortRecursive(list, left, mid);
            mergeSortRecursive(list, mid + 1, right);
            merge(list, left, mid, right);
        }
    }

    private static void merge(List<Ticket> list, int left, int mid, int right) {
        int size1 = mid - left + 1;
        int size2 = right - mid;

        Ticket[] leftArr = new Ticket[size1];
        Ticket[] rightArr = new Ticket[size2];

        for (int i = 0; i < size1; ++i)
            leftArr[i] = list.get(left + i);
        for (int j = 0; j < size2; ++j)
            rightArr[j] = list.get(mid + 1 + j);

        int i = 0, j = 0, k = left;

        while (i < size1 && j < size2) {
            if (compareTickets(leftArr[i], rightArr[j]) <= 0) {
                list.set(k++, leftArr[i++]);
            } else {
                list.set(k++, rightArr[j++]);
            }
        }

        while (i < size1) {
            list.set(k++, leftArr[i++]);
        }

        while (j < size2) {
            list.set(k++, rightArr[j++]);
        }
    }

    /**
     * Compares two tickets based on SLA first, then priority if needed.
     */
    private static int compareTickets(Ticket a, Ticket b) {
        int slaCompare = Integer.compare(a.getSlaHours(), b.getSlaHours());
        if (slaCompare != 0) return slaCompare;
        return priorityValue(a.getPriority()) - priorityValue(b.getPriority());
    }

    /**
     * Converts priority levels to numeric values.
     */
    private static int priorityValue(String priority) {
        return switch (priority.toLowerCase()) {
            case "critical" -> 1;
            case "medium" -> 2;
            case "low" -> 3;
            default -> 4;
        };
    }
}
